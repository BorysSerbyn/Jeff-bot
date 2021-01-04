package ca.borysserbyn.jeffbot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class TreeTask extends RecursiveAction {
    private final Node node;
    private final int depth;
    private final int THRESHOLD = 1;

    public TreeTask(Node node, int depth) {
        this.node = node;
        this.depth = depth;
    }

    public void compute() {
        if (depth > THRESHOLD) {//switch to single threaded after a certain depth
            node.addNodes(depth, node.getMaxDepth());
        }else{
            if(node.getChildNodes().isEmpty()){//if node is empty, add children with max depth = 1;
                node.addNodes(depth, depth+1);
            }
            List<TreeTask> subtasks = new ArrayList<TreeTask>();
            for (Node childNode : node.getChildNodes()) {
                subtasks.add(new TreeTask(childNode, depth + 1));
            }
            if(!subtasks.isEmpty()){
                invokeAll(subtasks);
            }
        }
    }
}
