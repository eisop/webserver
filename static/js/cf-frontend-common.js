/*

Online Python Tutor
https://github.com/pgbovine/OnlinePythonTutor/

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

// this is customized to my own Linode server:
// these are the REAL endpoints, accessed via jsonp. code is in ../../v4-cokapi/

//for deployment
var JAVA_BACKEND_URL;
//var ENDPOINT_ADDRESS;
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

// var domain = "http://pythontutor.com/"; // for deployment

var isExecutingCode = false; // nasty, nasty global

var appMode = 'edit'; // 'edit' or 'display'. also support
                      // 'visualize' for backward compatibility (same as 'display')

var pyInputAceEditor; // Ace editor object that contains the input code


var changeErrorStateListener; // change event listener on ace editor that change annotation type

function initAceEditor(height) {
  pyInputAceEditor = ace.edit('codeInputPane');
  var s = pyInputAceEditor.getSession();
  // tab -> 4 spaces
  s.setTabSize(4);
  s.setUseSoftTabs(true);

  // disable extraneous indicators:
  s.setFoldStyle('manual'); // no code folding indicators
  pyInputAceEditor.setHighlightActiveLine(false);
  pyInputAceEditor.setShowPrintMargin(false);
  pyInputAceEditor.setBehavioursEnabled(false);

  // auto-grow height as fit
  pyInputAceEditor.setOptions({minLines: 18, maxLines: 18});

  $('#codeInputPane').css('width', '100%');
  $('#codeInputPane').css('height', height + 'px'); // VERY IMPORTANT so that it works on I.E., ugh!

  // don't do real-time syntax checks:
  // https://github.com/ajaxorg/ace/wiki/Syntax-validation
  s.setOption("useWorker", false);

  setAceMode();

  pyInputAceEditor.focus();
}

var JAVA_BLANK_TEMPLATE = 'public class YourClassNameHere {\n\
    public static void main(String[] args) {\n\
\n\
    }\n\
}'

function setAceMode() {
  // var selectorVal = $('#pythonVersionSelector').val();
  var mod;
  var tabSize = 2;
    mod = 'java';
    // if blank empty, then initialize to a Java skeleton:
    if ($.trim(pyInputGetValue()) === '') {
      pyInputSetValue(JAVA_BLANK_TEMPLATE);
    }
  assert(mod);
  pyInputAceEditor.setTheme("ace/theme/eclipse");
  var s = pyInputAceEditor.getSession();
  s.setMode("ace/mode/" + mod);
  s.setTabSize(tabSize);
  s.setUseSoftTabs(true);

  // clear all error displays when switching modes
  var s = pyInputAceEditor.getSession();
  s.clearAnnotations();
  clearFrontendInfo();
}

var rawInputLst = []; // a list of strings inputted by the user in response to raw_input or mouse_input events


// each frontend must implement its own executeCode function
function executeCode() {
  alert("Configuration error. Need to override executeCode(). This is an empty stub.");
}

function setFronendInfo(lines, type) {
  if(type == "error"){
    $("#frontendInfoOutput").css('color', '#e93f34');
  } else if (type == "success"){
    $("#frontendInfoOutput").css('color', '#009900');
  }
  $("#frontendInfoOutput").html(lines.map(htmlspecialchars).join('<br/>'));
  $("#frontendInfoOutput").show();
}



function clearFrontendInfo() {
  $("#frontendInfoOutput").hide();
}

// abstraction so that we can use either CodeMirror or Ace as our code editor
function pyInputGetValue() {
    return pyInputAceEditor.getValue();
}

function pyInputSetValue(dat) {
    pyInputAceEditor.setValue(dat.rtrim() /* kill trailing spaces */,
                              -1 /* do NOT select after setting text */);
  // $('#urlOutput,#embedCodeOutput').val('');

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


var num414Tries = 0; // hacky global

// run at the END so that everything else can be initialized first
function genericOptFrontendReady() {

  // be friendly to the browser's forward and back buttons
  // thanks to http://benalman.com/projects/jquery-bbq-plugin/
  $(window).bind("hashchange", function(e) {
    // if you've got some preseeded code, then parse the entire query
    // string from scratch just like a page reload
    if ($.bbq.getState('code')) {
      parseQueryString();
    }
    // otherwise just do an incremental update
    else {
      var newMode = $.bbq.getState('mode');
      //console.log('hashchange:', newMode, window.location.hash);
      updateAppDisplay(newMode);
    }
  });
    initAceEditor(420);

  parseQueryString();

  // $(window).resize(redrawConnectors);

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
      if (num414Tries === 0) {
        num414Tries++;
        $("#executeBtn").click();
      } else {
        num414Tries = 0;
        
setFronendInfo(["Server error! Your code might be too long for this tool. Shorten your code and re-try."], "error");
      }
    } else {
      
setFronendInfo(["Server error! Your code might be taking too much time to run or using too much memory.",
                       "Report a bug to philip@pgbovine.net by clicking the 'Generate permanent link' button",
                       "at the bottom of this page and including a URL in your email."], "error");
    }

    doneExecutingCode();
  });

  clearFrontendInfo();

  // $("#embedLinkDiv").hide();
  $("#executeBtn").attr('disabled', false);
  $("#executeBtn").click(executeCodeFromScratch);

}


// sets globals such as rawInputLst, code input box, and toggle options
function parseQueryString() {
  var queryStrOptions = getQueryStringOptions();
  setToggleOptions(queryStrOptions);
  if (queryStrOptions.preseededCode) {
    pyInputSetValue(queryStrOptions.preseededCode);
  }
  if (queryStrOptions.rawInputLst) {
    rawInputLst = queryStrOptions.rawInputLst; // global
  }
  else {
    rawInputLst = [];
  }
  // ugh tricky -- always start in edit mode by default, and then
  // switch to display mode only after the code successfully executes
  appMode = 'edit';
  if ((queryStrOptions.appMode == 'display' ||
       queryStrOptions.appMode == 'visualize' /* 'visualize' is deprecated */) &&
      queryStrOptions.preseededCode /* jump to display only with pre-seeded code */) {
    executeCode(queryStrOptions.preseededCurInstr); // will switch to 'display' mode
  }
  $.bbq.removeState(); // clean up the URL no matter what
}

// parsing the URL query string hash
function getQueryStringOptions() {
  var ril = $.bbq.getState('rawInputLstJSON');
  var testCasesLstJSON = $.bbq.getState('testCasesJSON');
  // note that any of these can be 'undefined'
  return {preseededCode: $.bbq.getState('code'),
          preseededCurInstr: Number($.bbq.getState('curInstr')),
          verticalStack: $.bbq.getState('verticalStack'),
          appMode: $.bbq.getState('mode'),
          py: $.bbq.getState('py'),
          cumulative: $.bbq.getState('cumulative'),
          heapPrimitives: $.bbq.getState('heapPrimitives'),
          textReferences: $.bbq.getState('textReferences'),
          rawInputLst: ril ? $.parseJSON(ril) : undefined,
          testCasesLst: testCasesLstJSON ? $.parseJSON(testCasesLstJSON) : undefined
          };
}

function setToggleOptions(dat) {
  // ugh, ugly tristate due to the possibility of each being undefined
  if (dat.py !== undefined) {
    $('#pythonVersionSelector').val(dat.py);
  }
  if (dat.cumulative !== undefined) {
    $('#cumulativeModeSelector').val(dat.cumulative);
  }
  if (dat.heapPrimitives !== undefined) {
    $('#heapPrimitivesSelector').val(dat.heapPrimitives);
  }
  if (dat.textReferences !== undefined) {
    $('#textualMemoryLabelsSelector').val(dat.textReferences);
  }
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
    $("#codeInputWarnings").text("Write Java code here:");


    $(document).scrollTop(0); // scroll to top to make UX better on small monitors

    $.bbq.pushState({ mode: 'edit' }, 2 /* completely override other hash strings to keep URL clean */);
  }
  else if (appMode == 'display' || appMode == 'visualize' /* 'visualize' is deprecated */) {
    appMode = 'display'; // canonicalize
    $("#reportPane").show();
    // $("#javaOptionsPane").hide();
    $("#codeInputWarnings").text("Please fix the bug and check again!");

    doneExecutingCode();

    $(document).scrollTop(0); // scroll to top to make UX better on small monitors
    $.bbq.pushState({ mode: 'display' }, 2 /* completely override other hash strings to keep URL clean */);
  }
  else {
    assert(false);
  }

}


function executeCodeFromScratch() {
  // don't execute empty string:
  if ($.trim(pyInputGetValue()) == '') {
    
setFronendInfo(["Type in some code to visualize."], "error");
    return;
  }

  rawInputLst = []; // reset!
  executeCode();
}

function executeCodeWithRawInput(rawInputStr, curInstr) {
  rawInputLst.push(rawInputStr);
  console.log('executeCodeWithRawInput', rawInputStr, curInstr, rawInputLst);
  executeCode(curInstr);
}


function handleUncaughtExceptionFunc(trace) {
  if (trace.length == 1 && trace[0].line) {
    var errorLineNo = trace[0].line - 1; /* CodeMirror lines are zero-indexed */
    if (errorLineNo !== undefined && errorLineNo != NaN) {
        var s = pyInputAceEditor.getSession();
        s.setAnnotations([{row: errorLineNo,
                           type: 'error',
                           text: trace[0].exception_msg}]);
        pyInputAceEditor.gotoLine(errorLineNo + 1 /* one-indexed */);     
        if (trace[0].col !== undefined) {
          pyInputAceEditor.moveCursorTo(errorLineNo, trace[0].col);
        }
        pyInputAceEditor.focus();
    }
  }
}

function startExecutingCode() {
  $('#executeBtn').html("Please wait ... executing (takes up to 10 seconds)");
  $('#executeBtn').attr('disabled', true);
  isExecutingCode = true; // nasty global
}

function doneExecutingCode() {
  $('#executeBtn').html("Check");
  $('#executeBtn').attr('disabled', false);
  isExecutingCode = false; // nasty global
}

function enterDisplayMode() {
  updateAppDisplay('display');
}

function enterEditMode() {
  updateAppDisplay('edit');
}

function optFinishSuccessfulExecution() {
  enterDisplayMode(); // do this first!
  pyInputAceEditor.getSession().on('change', function(){
      var cursorPos = pyInputAceEditor.getCursorPosition();
  });
}

/*CheckerFramework: set the report table pane*/
function setReportPane(error_report) {
  $("#error_table tr").slice(1).remove();
  var error_table = document.getElementById("error_table");
  var count = 0;
  for (var errorIndex in error_report) {
    count++;
    var error = error_report[errorIndex];
    //add error to the last pos of err table
    var row = error_table.insertRow(-1);
    row.innerHTML = '<td>'+count+'</td>'+
        '<td>'+error.type+'</td>'+
        '<td>'+error.exception_msg+'</td>'+
        '<td>'+error.line+'</td>'+
        '<td>'+error.offset+'</td>';
    row.setAttribute("onmouseover",
      "changeColorOver(this,"+ (error.line-1) +","+(error.offset-1)+")");
    row.setAttribute("onmouseout", "changeColorOut(this)");
   } 
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
function bindChangeErrorStateListener(exec_function){
  changeErrorStateListener = exec_function;
  pyInputAceEditor.getSession().on("change", changeErrorStateListener);
}

/*CheckerFramework: register listener trigger function
@annotationsArray: annotationsArray parsed from function setErrorAnnotations(error_report)
return: debounced function handle of _changeErrorState*/
function registerChangeErrorState(annotationsArray) {
  var s = pyInputAceEditor.getSession();
  rowAnnotationMap = {};
  for(var i=0; i < annotationsArray.length; i++){
    var row = annotationsArray[i].row;
    if(!( row in rowAnnotationMap)){
      var indexArray = [];
      rowAnnotationMap[row] = indexArray;
    }
    rowAnnotationMap[row].push(i);
  }
  lastTimeRow = -1;
  function _changeErrorState(){
      var pos = pyInputAceEditor.getCursorPosition();
      var curRow = pos.row;
      var index;
      if(curRow == lastTimeRow)
        return;
      var indexArray = rowAnnotationMap[curRow];
      if( typeof indexArray != "undefined"){
        for(i in indexArray){
          var modAnnotation = annotationsArray[indexArray[i]];
          modAnnotation.type = 'info';
          modAnnotation.text = "previous " + modAnnotation.text;
          s.clearAnnotations();
          s.setAnnotations(annotationsArray);
        }
        delete rowAnnotationMap[curRow];
      }
   }
  function _debounceFunc(){
    $.doTimeout(20, _changeErrorState);
  }
  return _debounceFunc;
}

// TODO: cut reliance on the nasty rawInputLst global
function executeCodeAndCreateViz(codeToExec, backendOptionsObj,
                                 outputDiv,
                                 handleSuccessFunc) {
    /*CheckerFramework: callback function of checking user code
    *the element.type in error_report is related to java backend CheckerPrinter
    */
    function execCallback(dataFromBackend) {
      var user_code = dataFromBackend.code;
      var error_report = dataFromBackend.error_report;
      var annotationsArray = [];
      doneExecutingCode();// rain or shine, we're done executing!
      if(error_report[0].type == 'exception') {
        setFronendInfo([error_report[0].msg], "error");
        return;
      } else if(error_report[0].type == 'pass') {
        setFronendInfo([$("#type_system option[value="+backendOptionsObj.checker+"]").text() + " pass!"], "success");
      } else {
        pyInputSetValue(user_code);
        annotationsArray = setErrorAnnotations(error_report);
        setReportPane(error_report);
        optFinishSuccessfulExecution();
        bindChangeErrorStateListener(registerChangeErrorState(annotationsArray));
      }
      // tricky hacky reset
      num414Tries = 0;
    }
    // if you're in display mode, kick back into edit mode before
    // executing or else the display might not refresh properly ... ugh
    // krufty FIXME
    enterEditMode();
    unbindChangeErrorStateListener();
    clearFrontendInfo();
    pyInputAceEditor.getSession().clearAnnotations();
    startExecutingCode();

    backend_url = JAVA_BACKEND_URL;

      assert(backend_url);
      
      var inputObj = {};
      inputObj.usercode = codeToExec;
      // TODO: add options, arg, and stdin later ...
      inputObj.options = backendOptionsObj;
      $.ajax({
        url: backend_url,
        data: {frontend_data : JSON.stringify(inputObj)},
        dataType: "json",
        timeout:10000,
        success: execCallback
      });
}

$(document).ready(function() {
  init_java_backend_url();
});
