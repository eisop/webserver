/*This file is modified based on Online Python Tutor:

Online Python Tutor
https://github.com/pgbovine/OnlinePythonTutor/

====Original document showed as below====

Copyright (C) Philip J. Guo (philip@pgbovine.net)

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

// include this file BEFORE any OPT frontend file


// backend scripts to execute (Python 2 and 3 variants, if available)
// make two copies of ../web_exec.py and give them the following names,
// then change the first line (starting with #!) to the proper version
// of the Python interpreter (i.e., Python 2 or Python 3).
// Note that your hosting provider might have stringent rules for what
// kind of scripts are allowed to execute. For instance, my provider
// (Webfaction) seems to let scripts execute only if permissions are
// something like:
// -rwxr-xr-x 1 pgbovine pgbovine 2.5K Jul  5 22:46 web_exec_py2.py*
// (most notably, only the owner of the file should have write
//  permissions)

/*====deployment====*/
var JAVA_BACKEND_URL;

function init_java_backend_url() {
  var exec_url = $("#exec_url").attr("data-exec-url");
  if( typeof exec_url == "undefined" ) {
    exec_url = "/exec"; //fall back to hard code
  }
  JAVA_BACKEND_URL = exec_url;
  /*CheckerFramework: currently we only have java_backend and we don't request by using jsonp.
   *keep this here for future if decide using jsonp someday*/
  /*if(window.location.protocol == 'https:') {
    JAVA_BACKEND_URL = "https://" + ENDPOINT_ADDRESS + exec_url;
  } else {
    JAVA_BACKEND_URL = "http://" + ENDPOINT_ADDRESS + exec_url;
  }*/
}

/*====ace editor related===*/
var pyInputAceEditor; // Ace editor object that contains the input code

// silent flag for distinguish user editing and program setValue to ace text area
// tricky, but this way is suggested by ace: https://github.com/ajaxorg/ace/issues/503
ace_setValue_silent = false; 

function initAceEditor() {
  pyInputAceEditor = ace.edit('codeInputPane');
  // disable extraneous indicators:

  pyInputAceEditor.setHighlightActiveLine(false);
  pyInputAceEditor.setShowPrintMargin(false);
  pyInputAceEditor.setBehavioursEnabled(false);
  pyInputAceEditor.setTheme("ace/theme/eclipse");
  // auto-grow height as fit
  // Checker Framework:
  // (setOptions({minLines, maxLines}) function seems does NOT compatible with jquery resizable.
  // If using auto-grow rather than resizable, one should remove all resizable modules before comment out this line.
  // keep this line just for possible future use)
  // pyInputAceEditor.setOptions({minLines: 25, maxLines: 300});

  // initiate resizable container
  // $('#resizable_codeInput').css('width', '100%');
  // $('#resizable_codeInput').css('height', height + 'px');
  // $('#resizable_codeInput').css('max-height', '60%');

  $("#resizable_codeInput").resizable({
      resize: function( event, ui ) {
        pyInputAceEditor.resize();
      },
      handles:'s,e',
      delay: 100
    });

  // session settings
  var s = pyInputAceEditor.getSession();
  s.setFoldStyle('manual'); // no code folding indicators
  s.setMode("ace/mode/java");
  // tab -> 4 spaces
  s.setTabSize(4);
  s.setUseSoftTabs(true);
  // don't do real-time syntax checks:
  // https://github.com/ajaxorg/ace/wiki/Syntax-validation
  s.setOption("useWorker", false);

  var JAVA_BLANK_TEMPLATE = 'import org.checkerframework.checker.nullness.qual.Nullable;\n\
    class YourClassNameHere {\n\
      void foo(Object nn, @Nullable Object nbl) {\n\
	nn.toString(); // OK\n\
	nbl.toString(); // Error\n\
     }\n\
  }';
  if($.trim(pyInputGetValue()) === '') {
    pyInputSetValue(JAVA_BLANK_TEMPLATE);
  }

  pyInputAceEditor.focus();
}

// abstraction so that we can use either CodeMirror or Ace as our code editor
function pyInputGetValue() {
    return pyInputAceEditor.getValue();
}

function pyInputSetValue(dat) {
  // tricky way to distinguish user input with this program setValue
  // https://github.com/ajaxorg/ace/issues/503
  ace_setValue_silent = true;
  pyInputAceEditor.setValue(dat.rtrim() /* kill trailing spaces */,
                              -1 /* do NOT select after setting text */);
  ace_setValue_silent = false;

  clearFrontendInfo();
  // also scroll to top to make the UI more usable on smaller monitors
  $(document).scrollTop(0);
}

function pyInputGetScrollTop() {
  return pyInputAceEditor.getSession().getScrollTop();
}

function pyInputSetScrollTop(st) {
  pyInputAceEditor.getSession().setScrollTop(st);
}

/*====execute code====*/
var changeErrorStateListener; // change event listener on ace editor that change annotation type

var REMINDE_STRING = {
  WRITE_CODE : 'Write Java code here, then click <span id="execHref" onclick="executeCodeFromScratch()" >check</span>',
  FIX_BUG: 'Please fix the bug(s) and check again!',
  PASSED: 'Checker passed!',
  EXECUTING: 'Please wait ... executing (takes up to 10 seconds)'
}

function startExecutingCode() {
  $('#codeInputWarnings').html(REMINDE_STRING.EXECUTING);
  $('#executeBtn').html(REMINDE_STRING.EXECUTING);
  $('#executeBtn').attr('disabled', true);
}

function doneExecutingCode() {
  $('#executeBtn').html("Check");
  $('#executeBtn').attr('disabled', false);
}

/*CheckerFramework: Override backend Option*/
function getBaseBackendOptionsObj() {
  var ret = {checker: $('#type_system').val(),
             has_cfg: $('#cfg').is(':checked'),
             cfg_level: $('#cfg_level').val(),
             verbose: $('#verbose').is(':checked')};
  return ret;
}

function executeCodeFromScratch() {
  // don't execute empty string:
  if ($.trim(pyInputGetValue()) == '') {
setFronendInfo(["Type in some code to visualize."], "error");
    return;
  }
  executeCode();
}

function executeCode() {
  var backendOptionsObj = getBaseBackendOptionsObj();
  var codeToExec = pyInputGetValue();
  /*CheckerFramework: callback function of checking user code
    *the element.type in error_report is related to java backend CheckerPrinter
    */
    function execCallback(dataFromBackend) {
      if( !dataFromBackend ) {
        setFronendInfo(["Error: 324 empty response from server."], "error");
        doneExecutingCode();
        $("#codeInputWarnings").html(REMINDE_STRING.WRITE_CODE);
        return;
      }
      var user_code = dataFromBackend.code;
      var error_report = dataFromBackend.error_report;
      var annotationsArray = [];
      var backend_status = dataFromBackend.backend_status;
      doneExecutingCode();// rain or shine, we're done executing!
        if(backend_status == 'exception') {
          setFronendInfo([dataFromBackend.exception_msg], "error");
      }
      else if (backend_status == 'pass') {
        $("#codeInputWarnings").text(REMINDE_STRING.PASSED);
        setFronendInfo([$("#type_system option[value="+backendOptionsObj.checker+"]").text() + " passed!"], "success");
        setExecCmd(dataFromBackend.exec_cmd);
        enterDisplayMode();
      }
      else if (backend_status == 'diagnostic') {
        $("#codeInputWarnings").text(REMINDE_STRING.FIX_BUG);
        setExecCmd(dataFromBackend.exec_cmd);
        pyInputSetValue(user_code);
        annotationsArray = setErrorAnnotations(error_report);
        setErrorTable(error_report);
        optFinishSuccessfulExecution();
        bindChangeErrorStateListener(registerChangeErrorState(annotationsArray));
      }
    }

    // if you're in display mode, kick back into edit mode before
    // executing or else the display might not refresh properly ... ugh
    // krufty FIXME
    enterEditMode();
    clearExecCmd();
    clearFrontendInfo();
    clearErrorTable();
    unbindChangeErrorStateListener();
    pyInputAceEditor.getSession().clearAnnotations();

    startExecutingCode();

    backend_url = JAVA_BACKEND_URL;

    assert(backend_url);

    var inputObj = {};
    inputObj.usercode = codeToExec;
    inputObj.options = backendOptionsObj;

    $.ajax({
      url: backend_url,
      data: {frontend_data : JSON.stringify(inputObj)},
      dataType: "json",
      timeout:10000,
      success: execCallback
    });
}

/*====jquery bbq====*/
var appMode = 'edit'; // 'edit' or 'display'. also support
                      // 'visualize' for backward compatibility (same as 'display')

// sets globals such as rawInputLst, code input box, and toggle options
function parseQueryString() {
  var queryStrOptions = getQueryStringOptions();
  // parse url & set corresponding values
  if (queryStrOptions.precededCode) {
    pyInputSetValue(queryStrOptions.precededCode);
  }
  // typeSystem starts as nullness, might be changed based on url parsing result
  if(queryStrOptions.typeSystem) {
    $("#type_system").val(queryStrOptions.typeSystem);
  }
  // ugh tricky -- always start in edit mode by default, and then
  // switch to display mode only after the code successfully executes
  appMode = 'edit';
  if ((queryStrOptions.appMode == 'display' ||
       queryStrOptions.appMode == 'visualize' /* 'visualize' is deprecated */) &&
      queryStrOptions.precededCode /* jump to display only with pre-seeded code */) {
    executeCode(queryStrOptions.precededCodeCurInstr); // will switch to 'display' mode
  }
  $.bbq.removeState(); // clean up the URL no matter what
}

// parsing the URL query string hash
function getQueryStringOptions() {
  return {precededCode: $.bbq.getState('code'),
          typeSystem: $.bbq.getState('typeSystem'),
          precededCodeCurInstr: Number($.bbq.getState('curInstr')),
          appMode: $.bbq.getState('mode')
          };
}

/*====general frontend display functions====*/
function setFronendInfo(lines, type) {
  if(type == "error") {
    $("#frontendInfoOutput").css('color', '#e93f34');
  } else if (type == "success") {
    $("#frontendInfoOutput").css('color', '#009900');
  }
  $("#frontendInfoOutput").html(lines.map(htmlspecialchars).join('<br/>'));
  $("#frontendInfoOutput").show();
}

function clearFrontendInfo() {
  $("#frontendInfoOutput").hide();
}

// sets the global appMode variable if relevant and also the URL hash to
// support some amount of Web browser back button goodness
function updateAppDisplay(newAppMode) {
  // idempotence is VERY important here
  if (newAppMode == appMode) {
    return;
  }

  appMode = newAppMode; // global!

  if (appMode === undefined || appMode == 'edit') {
    appMode = 'edit'; // canonicalize
    pyInputAceEditor.setReadOnly(false);
  
    $("#reportPane").hide();
    // $("#javaOptionsPane").show();
    $("#codeInputWarnings").html(REMINDE_STRING.WRITE_CODE);

    $(document).scrollTop(0); // scroll to top to make UX better on small monitors

    $.bbq.pushState({ mode: 'edit' }, 2 /* completely override other hash strings to keep URL clean */);
  }
  else if (appMode == 'display' || appMode == 'visualize' /* 'visualize' is deprecated */) {
    appMode = 'display'; // canonicalize
    $("#reportPane").show();

    doneExecutingCode();

    $(document).scrollTop(0); // scroll to top to make UX better on small monitors
    $.bbq.pushState({ mode: 'display' }, 2 /* completely override other hash strings to keep URL clean */);
  }
  else {
    assert(false);
  }
}

function selectedCheckerOnChange() {
  var checker_value = $("#type_system").val();
  var checker_name = $("#type_system option[value="+checker_value+"]").text();
  var href = $("#examplesPane p[data-checker-type="+checker_value+"]").children("a.manualLink").attr("href");

  $("#selectedCheckerManual").attr("href", href).text("manual of " + checker_name);


}

// generate a permanent link with user input code and selected type system encoded
// eg. hostName/#typeSystem=nullness&code=whatTheUserInputIsInTheEditor
function codePermanentLinkGeneration() {
  var checker_value = $("#type_system").val();
  var typeSystemURL = encodeURI(checker_value);
  typeSystemURL = "#typeSystem=" + typeSystemURL;

  var input = pyInputGetValue(); 
  var inputURL = encodeURI(input);
  inputURL = "&code=" + inputURL;

  var curUrl = window.location.host + '/';
  document.getElementById("codePermanentLink").value = (curUrl + typeSystemURL + inputURL);
}


/*====general functions====*/
// run at the END so that everything else can be initialized first
function genericOptFrontendReady() {
  // be friendly to the browser's forward and back buttons
  // thanks to http://benalman.com/projects/jquery-bbq-plugin/
  $(window).bind("hashchange", function(e) {
    
    // parse url hash parameters and set corresponding values if 'code' or 'typeSystem' are encoded
    // linkGen feature generates url containing both code & typeSystem
    // use OR condition for future extension
    // allowing url with only code or typeSystem encoding be parsed here
    if ($.bbq.getState('code') || $.bbq.getState('typeSystem')) {
      parseQueryString();
    }
    // otherwise just do an incremental update
    else {
      var newMode = $.bbq.getState('mode');
      // console.log('hashchange:', newMode, window.location.hash);
      updateAppDisplay(newMode);
    }
  });

  initAceEditor();

  parseQueryString();

  // register a generic AJAX error handler
  $(document).ajaxError(function(evt, jqxhr, settings, exception) {

    /*
      This jqxhr.responseText might be indicative of the URL being too
      long, since the error message returned by the server is something
      like this in nginx:

<html>
<head><title>414 Request-URI Too Large</title></head>
<body bgcolor="white">
<center><h1>414 Request-URI Too Large</h1></center>
<hr><center>nginx</center>
</body>
</html>

      Note that you'll probably need to customize this check for your server. */
    if (jqxhr && jqxhr.responseText.indexOf('414') >= 0) {

      // ok this is an UBER UBER hack. If this happens just once, then
      // force click the "Visualize Execution" button again and re-try.
      // why? what's the difference the second time around? the diffs_json
      // parameter (derived from deltaObj) will be *empty* the second time
      // around since it gets reset on every execution. if diffs_json is
      // HUGE, then that might force the URL to be too big without your
      // code necessarily being too big, so give it a second shot with an
      // empty diffs_json. if it STILL fails, then display the error
      // message and give up.
setFronendInfo(["Server error! Your code might be too long for this tool. Shorten your code and re-try."], "error");
    } else {
setFronendInfo(["Server error! Your code might be taking too much time to run or using too much memory.",
                       "Please report a bug to admin."], "error");
    }

    doneExecutingCode();
  });

  clearFrontendInfo();

  $("#executeBtn").attr('disabled', false);
  $("#executeBtn").click(executeCodeFromScratch);
}

function enterDisplayMode() {
  updateAppDisplay('display');
}

function enterEditMode() {
  updateAppDisplay('edit');
}

function optFinishSuccessfulExecution() {
  enterDisplayMode(); // do this first!
  pyInputAceEditor.getSession().on('change', function() {
      var cursorPos = pyInputAceEditor.getCursorPosition();
  });
}

/*====error annotation and error report====*/
function setExecCmd(exec_cmd) {
  $("#exec_cmd").html("Executed command: <code>" + exec_cmd + " afile.java</code>");
  $("#exec_cmd").show();
}

function clearExecCmd() {
  $("#exec_cmd").hide();
}

function clearErrorTable() {
  $("#error_table").hide();
}

/*CheckerFramework: set the report table pane*/
function setErrorTable(error_report) {
  $("#error_table tr").slice(1).remove();
  var error_table = document.getElementById("error_table");
  var count = 0;
  for (var errorIndex in error_report) {
    count++;
    var error = error_report[errorIndex];
    // add error to the last pos of err table
    var row = error_table.insertRow(-1);

    //escape html special characters in the exception message.   
    exception_msg = htmlspecialchars(error.exception_msg);
    //Replace "\n" by html <br/> tag.
    exception_msg = exception_msg.replace(/\n/g,"<br/>&nbsp");

    row.innerHTML = '<td>'+count+'</td>'+
        '<td>'+error.type+'</td>'+
        '<td>'+exception_msg+'</td>'+
        '<td>'+error.line+'</td>'+
        '<td>'+error.offset+'</td>';
    row.setAttribute("onmouseover",
      "changeColorOver(this,"+ (error.line-1) +","+(error.offset-1)+")");
    row.setAttribute("onmouseout", "changeColorOut(this)");
   } 
   $("#error_table").show();
}

/*CheckerFramework: set annotations to ace editor
return annotationsArray parsed from error_report*/
function setErrorAnnotations(error_report) {
  var annotationsArray = [];
  for (var errorIndex in error_report) {
    var error = error_report[errorIndex];
    var annotation = {
      row: error.line - 1,
      column: error.offset -1,
      text: error.exception_msg,
      type: error.type
    }; 
     /*CHECKER_FRAMWORK:
  One problem of ace editor is it can only show one annotation on
  one line at the same time. Thus, if a line has multiple errors, the
  annotation would only show the one that comes first in this array.
  (but all errors would show on the error_table)*/
    annotationsArray.push(annotation);
    var s = pyInputAceEditor.getSession();
    s.clearAnnotations();
    s.setAnnotations(annotationsArray);
   }
   return annotationsArray;
}

/*CheckerFramework: helper function of error table*/
function changeColorOver(e, line, column) {
    e.style.backgroundColor = "#d4d4d4";
    pyInputAceEditor.navigateTo(line, column);
        
}

/*CheckerFramework: helper function of error table*/
function changeColorOut(e) {
    e.style.backgroundColor = "#ffffff";
  }

/*CheckerFramework: unbind listener from ace editor*/
function unbindChangeErrorStateListener() {
  var s = pyInputAceEditor.getSession();
  s.clearAnnotations();
  s.removeListener("change", changeErrorStateListener);
}

/*CheckerFramework: bind listener with exec_function to ace editor*/
function bindChangeErrorStateListener(exec_function) {
  changeErrorStateListener = exec_function;
  pyInputAceEditor.getSession().on("change", changeErrorStateListener);
}

/*CheckerFramework: register listener trigger function
@annotationsArray: annotationsArray parsed from function setErrorAnnotations(error_report)
return: debounced function handle of _changeErrorState*/
function registerChangeErrorState(annotationsArray) {
  var s = pyInputAceEditor.getSession();
  rowAnnotationMap = {};
  for(var i=0; i < annotationsArray.length; i++) {
    var row = annotationsArray[i].row;
    if(!( row in rowAnnotationMap)) {
      var indexArray = [];
      rowAnnotationMap[row] = indexArray;
    }
    rowAnnotationMap[row].push(i);
  }
  lastTimeRow = -1;
  function _changeErrorState() {
      var pos = pyInputAceEditor.getCursorPosition();
      var curRow = pos.row;
      var index;
      if(curRow == lastTimeRow)
        return;
      var indexArray = rowAnnotationMap[curRow];
      if( typeof indexArray != "undefined") {
        for(i in indexArray) {
          var modAnnotation = annotationsArray[indexArray[i]];
          modAnnotation.type = 'info';
          modAnnotation.text = "previous " + modAnnotation.text;
          s.clearAnnotations();
          s.setAnnotations(annotationsArray);
        }
        delete rowAnnotationMap[curRow];
      }
   }
  function _debounceFunc() {
    $.doTimeout(20, _changeErrorState);
  }
  return _debounceFunc;
}

/*====util functions====*/
String.prototype.rtrim = function() {
  return this.replace(/\s*$/g, "");
}

function assert(cond) {
  if (!cond) {
    alert("Assertion Failure (see console log for backtrace)");
    throw 'Assertion Failure';
  }
}

// taken from http://www.toao.net/32-my-htmlspecialchars-function-for-javascript
function htmlspecialchars(str) {
  if (typeof(str) == "string") {
    str = str.replace(/&/g, "&amp;"); /* must do &amp; first */

    // ignore these for now ...
    // str = str.replace(/"/g, "&quot;");
    // str = str.replace(/'/g, "&#039;");

    str = str.replace(/</g, "&lt;");
    str = str.replace(/>/g, "&gt;");

    // replace spaces:
    str = str.replace(/ /g, "&nbsp;");

    // replace tab as four spaces:
    str = str.replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
  }
  return str;
}

$(document).ready(function() {
  init_java_backend_url();
  genericOptFrontendReady(); // initialize at the end
  // add onchange listener to update codeInputWarnings whenever user editing the code
  pyInputAceEditor.getSession().on("change", function() {
      function _updateToWriteCode() {
        $("#codeInputWarnings").html(REMINDE_STRING.WRITE_CODE)
      }
    if (ace_setValue_silent) return;
    $.doTimeout(300, _updateToWriteCode);
  });
});
