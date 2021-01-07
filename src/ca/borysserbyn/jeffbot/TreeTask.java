package ca.borysserbyn.jeffbot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class TreeTask extends RecursiveAction {
    private final Node node;
    private final int depth;
    private final int THRESHOLD = 1;
    private final boolean secondTry;

    public TreeTask(Node node, int depth, boolean secondTry) {
        this.node = node;
        this.depth = depth;
        this.secondTry = secondTry;
    }

    public void compute() {
        if (depth > THRESHOLD) {//switch to single threaded after a certain depth
            node.addNodes(depth, node.getMaxDepth(), false);
        }else{
            if(node.getChildNodes().isEmpty()){//if node is empty, add children with max depth = 1;
                node.addNodes(0, 1, secondTry);
            }
            List<TreeTask> subtasks = new ArrayList<TreeTask>();
            for (Node childNode : node.getChildNodes()) {
                subtasks.add(new TreeTask(childNode, depth + 1, false));
            }
            if(!subtasks.isEmpty()){
                invokeAll(subtasks);
            }
        }
    }
}
