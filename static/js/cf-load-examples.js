/*Checker Framework:
This file manages the examples location in the server.

This file also providing functions of loading examples from server to frontend .
*/

var static_url_prefix;
var JAVA_EXAMPLES = {
  /*Nullness Checker examples*/
  NullnessExampleWithWarningsLink: 'examples/nullness/NullnessExampleWithWarnings.java',
  NullnessExampleLink: 'examples/nullness/NullnessExample.java',

  /*Optional Checker examples*/
  OptionalExampleWithWarningsLink: 'examples/optional/OptionalExampleWithWarnings.java',
  OptionalExampleLink: 'examples/optional/OptionalExample.java',

  /*MapKeyChecker examples*/
  MapKeyExampleWithWarningsLink: 'examples/map_key/MapKeyExampleWithWarnings.java',

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
  SimpleDemoWithWarningsLink: 'examples/units/SimpleDemoWithWarnings.java',
  MethodsDemoWithWarningsLink: 'examples/units/MethodsDemoWithWarnings.java',
  PolyUnitDemoWithWarningsLink: 'examples/units/PolyUnitDemoWithWarnings.java',

  /*Constant Checker example*/
  ConstantExampleLink: 'examples/constant_example/ConstantExample.java'
};

/**clear all frontend display infos
*/
function clearAllFrontendDisplay() {
  unbindChangeErrorStateListener();
  clearErrorTable();
  clearFrontendInfo();
  clearExecCmd();
  pyInputAceEditor.getSession().clearAnnotations();
}

function initExampleLinks() {
   // canned examples
  $(".exampleLink").click(function() {
    var myId = $(this).attr('id');
    var exFile;
    exFile = JAVA_EXAMPLES[myId];
    if( typeof exFile == 'undefined') {
      setFronendInfo(["Sorry, cannot find that example on our server:-("], "error");
      return false;
    }
    $.get(static_url_prefix + exFile, function(dat) {
      /*CheckerFramework: unbind Ace editor Change listener */
      unbindChangeErrorStateListener();
      pyInputSetValue(dat);
      clearExecCmd();
      clearAllFrontendDisplay();
      enterEditMode();
    }, 'text' /* data type - set to text or else jQuery tries to EXECUTE the JS example code! */);

    /*CheckerFramework: bind example with the coorelated checker*/
    var checkerType = $(this).parent().attr('data-checker-type');
    $("#type_system").val(checkerType);
    selectedCheckerOnChange(); //update the manual link in select-checker section
    return false; // prevent 'a' click from going to an actual link
  });
}

function init_static_url() {
   //init static url prefix
  static_url_prefix = $("#static_url_prefix").attr('data-static-url-prefix');
  if ( typeof static_url_prefix == "undefined" ) {
    static_url_prefix = "/static/"; //fall back to hard code
  }
}

$(document).ready(function() {
  init_static_url();
  initExampleLinks();
});
