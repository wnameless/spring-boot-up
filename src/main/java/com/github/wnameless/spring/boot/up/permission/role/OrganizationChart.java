package com.github.wnameless.spring.boot.up.permission.role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import net.sf.rubycollect4j.Ruby;

public interface OrganizationChart {

  List<RolePosition> getRolePositions();

  default TreeNode<Role> toTreeNode() {
    var positions = Ruby.Array.copyOf(getRolePositions());
    var topPositions = positions.findAll(p -> p.getManager().isEmpty());
    if (topPositions.size() != 1) throw new IllegalStateException("More than 1 root found");
    positions.removeAll(topPositions);

    var topPosition = topPositions.first();
    var root = new ArrayMultiTreeNode<Role>(topPosition.toRole());
    List<TreeNode<Role>> subPositions = Arrays.asList(root);
    do {
      subPositions = addSubPositions(subPositions, positions);
    } while (subPositions.size() != 0);

    return root;
  }

  private static List<TreeNode<Role>> addSubPositions(List<TreeNode<Role>> managerNodes,
      List<RolePosition> positions) {
    var subNodes = new ArrayList<TreeNode<Role>>();

    for (var managerNode : managerNodes) {
      var subPositions = Ruby.Array.copyOf(positions).findAll(p -> p.getManager()
          .map(m -> m.getRoleName().equals(managerNode.data().getRoleName())).orElse(false));
      positions.removeAll(subPositions);

      for (var subPosition : subPositions) {
        var subNode = new ArrayMultiTreeNode<Role>(subPosition.toRole());
        managerNode.add(subNode);
        subNodes.add(subNode);
      }
    }

    return subNodes;
  }

}
