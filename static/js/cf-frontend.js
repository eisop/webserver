/*Checker Framework:
This js is modified based on opt-frontend.js from Online Python Tutor
Major Modification:
1. remove all code related to together js (we don't need share editing currently)
2. simplify all code related to consideration of  multi-language to single-language JAVA
3. get static file url-prefix from index page
4. re-write getBaseBackendObj to adapt to checker framework backend parameters
5. change get_example_file function to dynamic decide checker type of a specific example

===origin comment of opt-frontend.js shown as below===
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

/*CheckerFramework: Override backend Option*/
function getBaseBackendOptionsObj() {
  var ret = {checker: $('#type_system').val(),
             has_cfg: $('#cfg').is(':checked'),
             cfg_level: $('#cfg_level').val(),
             verbose: $('#verbose').is(':checked')};
  return ret;
}

function executeCode(forceStartingInstr, forceRawInputLst) {
  if (forceRawInputLst !== undefined) {
    rawInputLst = forceRawInputLst; // UGLY global across modules, FIXME
  }
  var backendOptionsObj = getBaseBackendOptionsObj();

  /*CheckerFramework: remove un-need parameters*/
  executeCodeAndCreateViz(pyInputGetValue(), backendOptionsObj,
                          'pyOutputPane',
                          optFinishSuccessfulExecution);
}

function initAceAndOptions() {
  setAceMode(); // update syntax highlighting mode
}
var static_url_prefix;
var JAVA_EXAMPLES = {
  /*Nullness Checker examples*/
  NullnessExampleWithWarningsLink: 'examples/nullness/NullnessExampleWithWarnings.java',
  NullnessExampleLink: 'examples/nullness/NullnessExample.java',
  /*MapKeyChecker usually automatically called by other Checkers,
   especially with Nullness Checker, thus put this here*/
  MapKeyExampleWithWarningsLink: 'examples/nullness/MapKeyExampleWithWarnings.java', 


  /*Interning Checker examples*/
  InterningExampleLink: 'examples/interning/InterningExample.java',
  InterningExampleWithWarningsLink: 'examples/interning/InterningExampleWithWarnings.java',

  /*Lock Checker examples*/
  GuardedByExampleWithWarningsLink: 'examples/lock/GuardedByExampleWithWarnings.java',
  HoldingExampleWithWarningsLink: 'examples/lock/HoldingExampleWithWarnings.java',
  EnsuresLockHeldExampleLink: 'examples/lock/EnsuresLockHeldExample.java',
  LockingFreeExampleWithWarningsLink: 'examples/lock/LockingFreeExampleWithWarnings.java',

  /*Fake Enum Checker examples*/
  FakeEnumExampleWithWarningsLink: 'examples/fake_enum/FakeEnumExampleWithWarnings.java',

  /*Format String Checker examples*/
  FormatStringExampleWithWarningsLink: 'examples/format_string/FormatStringExampleWithWarnings.java',
  FormatStringMissedAlarmsLink: 'examples/format_string/FormatStringMissedAlarms.java',

  /*Regex Checker examples*/
  RegexExampleWithWarningsLink: 'examples/regex/RegexExampleWithWarnings.java',
  RegexConcatenationExampleLink: 'examples/regex/RegexConcatenationExample.java',

  /*Linear Checker examples*/
  LinearExampleWithWarningsLink: 'examples/linear/LinearExampleWithWarnings.java',

  /*IGJ Checker examples*/
  IGJExampleWithWarningsLink: 'examples/igj/IGJExampleWithWarnings.java',

  /*Javari Checker examples*/
  JavariExampleWithWarningsLink: 'examples/javari/JavariExampleWithWarnings.java',

  /*Tainting Checker examples*/
  TaintingExampleWithWarningsLink: 'examples/tainting/TaintingExampleWithWarnings.java',

  /*Signature Checker examples*/
  SignatureExampleWithWarningsLink: 'examples/signature/SignatureExampleWithWarnings.java',
  SignatureExampleLink: 'examples/signature/SignatureExample.java',

  /*GUIEffect Checker examples*/
  GUIEffectExampleWithWarningsLink: 'examples/gui_effect/GUIEffectExampleWithWarnings.java',

  /*Units Checker examples*/
  AdditionWithWarningsLink: 'examples/units/AdditionWithWarnings.java',
  SubtractionWithWarningsLink: 'examples/units/SubtractionWithWarnings.java',
  MultiplesWithWarningsLink: 'examples/units/MultiplesWithWarnings.java',
  DivisionWithWarningsLink: 'examples/units/DivisionWithWarnings.java',

};

$(document).ready(function() {
  //init static url prefix
  static_url_prefix = $("#static_url_prefix").attr('data-static-url-prefix');
  if ( typeof static_url_prefix == "undefined" ) {
    static_url_prefix = "/static/"; //fall back to hard code
  }
  // canned examples
  $(".exampleLink").click(function() {
    var myId = $(this).attr('id');
    var exFile;
    exFile = JAVA_EXAMPLES[myId];
    if( typeof exFile == 'undefined') {
      setFronendInfo(["Sorry, cannot find that example on our server:("], "error");
      return false;
    }
    $.get(static_url_prefix + exFile, function(dat) {
      /*CheckerFramework: unbind Ace editor Change listener */
      unbindChangeErrorStateListener();
      pyInputSetValue(dat);
      initAceAndOptions();
      enterEditMode();
    }, 'text' /* data type - set to text or else jQuery tries to EXECUTE the JS example code! */);

    /*CheckerFramework: bind example with the coorelated checker*/
    var checkerType = $(this).parent().attr('data-checker-type');
    $("#type_system").val(checkerType);

    return false; // prevent 'a' click from going to an actual link
  });

  genericOptFrontendReady(); // initialize at the end
  initAceAndOptions(); // do this after genericOptFrontendReady
});
