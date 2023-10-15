package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.MutablePair;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

public interface OrganizationTree {

  String getOrganizationName();

  void setOrganizationName(String organizationName);

  List<Entry<String, String>> getNodeVectors();

  void setNodeVectors(List<Entry<String, String>> vectors);

  default TreeNode<String> toTreeNode() {
    var source = getNodeVectors();
    var treeNode = new ArrayMultiTreeNode<>(source.get(0).getKey());
    for (int i = 1; i < source.size(); i++) {
      var vector = source.get(i);
      treeNode.find(vector.getKey()).add(new ArrayMultiTreeNode<>(vector.getValue()));
    }
    return treeNode;
  }

  default void readTreeNode(TreeNode<String> source) {
    List<Entry<String, String>> vectors = new ArrayList<>();
    vectors.add(new MutablePair<>(source.data(), source.data()));
    for (var subTree : source.subtrees()) {
      traverseVectors(vectors, source, subTree);
    }
    setNodeVectors(vectors);
  }

  private static void traverseVectors(List<Entry<String, String>> vectors, TreeNode<String> parent,
      TreeNode<String> child) {
    vectors.add(new MutablePair<>(parent.data(), child.data()));
    if (!child.isLeaf()) {
      for (var subTree : child.subtrees()) {
        traverseVectors(vectors, child, subTree);
      }
    }
  }

}
