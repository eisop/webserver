package org.checkerframework.dataflow.cfg;

/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.analysis.Analysis;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.UnderlyingAST.CFGMethod;
import org.checkerframework.dataflow.cfg.UnderlyingAST.CFGStatement;
import org.checkerframework.dataflow.cfg.block.Block;
import org.checkerframework.javacutil.ErrorReporter;
import org.checkerframework.dataflow.cfg.DOTCFGVisualizer;

/**
 * Generate a graph description in the DOT language of a control graph.
 * Then, output the graph with its corresponding class & method names to stdout as YAML string
 * Output format:
 * -
 *   checker: checkerName
 *   cfg: [{className: c, methodName: m, dotString: d}, {className: cc, methodName: mm, dotString: dd}]
 */
public class DOTstringCFGVisualizer<
                A extends AbstractValue<A>, S extends Store<S>, T extends TransferFunction<A, S>>
        extends DOTCFGVisualizer<A, S, T>  {
		 
	/** {@inheritDoc} */
    @Override
    public /*@Nullable*/ Map<String, Object> visualize(
            ControlFlowGraph cfg, Block entry, /*@Nullable*/ Analysis<A, S, T> analysis) {
       
        String clsname="", methname="";
        UnderlyingAST ast = cfg.underlyingAST;
        
        if (ast.getKind() == UnderlyingAST.Kind.ARBITRARY_CODE) {
            CFGStatement cfgs = (CFGStatement) ast;
            clsname = cfgs.getClassTree().getSimpleName().toString();
            methname = "initializer";

        } else if (ast.getKind() == UnderlyingAST.Kind.METHOD) {
            CFGMethod cfgm = (CFGMethod) ast;
            clsname = cfgm.getClassTree().getSimpleName().toString();
            methname = cfgm.getMethod().getName().toString();

        } else {
            ErrorReporter.errorAbort(
                    "Unexpected AST kind: " + ast.getKind() + " value: " + ast.toString());
        }

        String rawDotgraph = generateDotGraph(cfg, entry, analysis);
        String dotgraph = rawDotgraph.replaceAll("\\\\\"", "&squote;");
        dotgraph = dotgraph.replaceAll("\"", "&quote;"); //no, but 5 gives two
        dotgraph = dotgraph.replaceAll("\\\\l", "\\\\\\\\l");
        dotgraph = dotgraph.replaceAll(":", "&colon;");
        
    	System.out.println("-");
    	System.out.println("  checker: " + super.checkerName);
    	System.out.println("  className: " + clsname);
    	System.out.println("  methodName: " + methname);
    	System.out.println("  dotString: " + "\"" + dotgraph + "\"");
       
        Map<String, Object> res = new HashMap<>();
        return res;
    }

  

    /** intentionally empty, no output file involved (just don't do anything with this inherited method or override?) */
    protected String dotOutputFileName(UnderlyingAST ast) {
    	return "";
    }

    /**
     * Write a string containing information about each method and its corresponding dotstring in YAML format
     * use YAML: shutdown is invoked for every checker and sometimes multiple times with one compile command
     * YAML behaves nicely even when multiple groups are print out whereas JSON format will be violated
     */
    @Override
    public void shutdown() {

    }
}
