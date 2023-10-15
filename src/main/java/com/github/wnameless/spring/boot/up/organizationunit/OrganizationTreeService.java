package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Optional;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

public interface OrganizationTreeService<OT extends OrganizationTree, ID> {

  OT newOrganizationTree();

  OrganizationTreeRepository<OT, ID> getRepository();

  default String getOrganizationName() {
    return "Organization";
  }

  default OT getRoot() {
    Optional<OT> rootOpt = getRepository().findByOrganizationName(getOrganizationName());
    return rootOpt.orElseGet(() -> {
      var root = newOrganizationTree();
      root.setOrganizationName(getOrganizationName());
      root.readTreeNode(new ArrayMultiTreeNode<String>(getOrganizationName()));
      return getRepository().save(root);
    });
  }

  default Optional<TreeNode<String>> findNode(String orgUnitName) {
    Optional<OT> rootOpt = getRepository().findByOrganizationName(getOrganizationName());
    return Optional.ofNullable(rootOpt.orElseGet(() -> {
      var ot = newOrganizationTree();
      ot.setOrganizationName(getOrganizationName());
      ot.readTreeNode(new ArrayMultiTreeNode<String>(getOrganizationName()));
      return getRepository().save(ot);
    }).toTreeNode().find(orgUnitName));
  }

  default OT findOrCreateParentFor(String target, String parent) {
    var nodeOpt = findNode(target);
    if (nodeOpt.isPresent()) {
      var node = nodeOpt.get();
      if (node.isRoot()) {
        var newRoot = new ArrayMultiTreeNode<>(parent);
        newRoot.add(node);

        var ot = getRoot();
        ot.readTreeNode(newRoot);
        getRepository().save(ot);
      }

      var nodeParent = node.parent();
      if (!nodeParent.data().equals(parent)) {
        nodeParent.dropSubtree(node);
        var insertNode = nodeParent.find(parent);
        if (insertNode == null) insertNode = new ArrayMultiTreeNode<>(parent);
        insertNode.add(node);
        nodeParent.add(insertNode);

        var ot = getRoot();
        ot.readTreeNode(nodeParent.root());
        getRepository().save(ot);
      }
    }
    return getRoot();
  }

  default boolean changeOrganizationUnitName(String oldName, String newName) {
    var root = getRoot();
    var targetNode = root.toTreeNode().find(oldName);
    if (targetNode == null) return false;

    targetNode.setData(newName);
    root.readTreeNode(targetNode.root());
    getRepository().save(root);
    return true;
  }

}
