package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

public interface OrganizationUnitService<ID> {

  List<? extends OrganizationUnitRepository<? extends OrganizationUnit, ID>> getOrganizationUnitRepositories();

  Set<OrganizationTree> getOrganizationTrees();

  default Optional<? extends OrganizationUnit> findOrganizationUnit(String organizationUnitName) {
    var rootOpt = getOrganizationTrees().stream().filter(
        ot -> Objects.equals(ot.getDefaultRoot().getOrganizationUnitName(), organizationUnitName))
        .findFirst();
    if (rootOpt.isPresent()) return Optional.of(rootOpt.get().getDefaultRoot());

    for (var repo : getOrganizationUnitRepositories()) {
      var ouOpt = repo.findByOrganizationUnitName(organizationUnitName);
      if (ouOpt.isPresent()) return ouOpt;
    }
    return Optional.empty();
  }

  default Optional<? extends OrganizationUnit> findOrganizationUnit(String organizationUnitName,
      Class<?> organizationUnitType) {
    if (SimpleOrganizationUnit.class.isAssignableFrom(organizationUnitType)) {
      var rootOpt = getOrganizationTrees().stream().filter(
          ot -> Objects.equals(ot.getDefaultRoot().getOrganizationUnitName(), organizationUnitName))
          .findFirst();
      if (rootOpt.isPresent()) return Optional.of(rootOpt.get().getDefaultRoot());
    }

    for (var repo : getOrganizationUnitRepositories()) {
      var ouOpt = repo.findByOrganizationUnitName(organizationUnitName);
      if (ouOpt.isPresent()) {
        if (organizationUnitType.isAssignableFrom(ouOpt.get().getClass())) {
          return ouOpt;
        }
      }
    }
    return Optional.empty();
  }

  default TreeNode<SimpleOrganizationUnit> toTreeNode(String organizationUnitName) {
    var ouOpt = findOrganizationUnit(organizationUnitName);
    if (ouOpt.isEmpty()) return null;
    if (!(isTreeNode(ouOpt.get()))) return null;
    return toTreeNode(ouOpt.get());
  }

  default TreeNode<SimpleOrganizationUnit> toTreeNode(OrganizationUnit organizationUnit) {
    if (organizationUnit == null) return null;

    var oues = getAllTreeOrganizationUnits(organizationUnit);
    var rootOpt =
        oues.stream().filter(ou -> ou.getParentOrganizationUnitName() == null).findFirst();
    if (rootOpt.isEmpty()) return null;

    var root = rootOpt.get();
    oues.remove(root);
    var rootNode = new ArrayMultiTreeNode<SimpleOrganizationUnit>(new SimpleOrganizationUnit(root));

    List<TreeNode<SimpleOrganizationUnit>> leaves = new ArrayList<>(Arrays.asList(rootNode));
    do {
      leaves = buildTree(leaves, oues);
    } while (!leaves.isEmpty());

    return rootNode;
  }

  private static List<TreeNode<SimpleOrganizationUnit>> buildTree(
      List<TreeNode<SimpleOrganizationUnit>> leaves,
      List<SimpleOrganizationUnit> simpleOrganizationUnits) {
    var newLeaves = new ArrayList<TreeNode<SimpleOrganizationUnit>>();

    for (var leaf : leaves) {
      var targets = simpleOrganizationUnits.stream().filter(sou -> Objects
          .equals(leaf.data().getOrganizationUnitName(), sou.getParentOrganizationUnitName()))
          .toList();
      simpleOrganizationUnits.removeAll(targets);
      for (var target : targets) {
        var newLeaf = new ArrayMultiTreeNode<>(target);
        leaf.add(newLeaf);
        newLeaves.add(newLeaf);
      }
    }

    return newLeaves;
  }

  default List<Entry<String, String>> toNodeVectors(TreeNode<SimpleOrganizationUnit> source) {
    List<Entry<String, String>> vectors = new ArrayList<>();
    vectors.add(new MutablePair<>(source.data().getOrganizationUnitName(),
        source.data().getOrganizationUnitName()));
    for (var subTree : source.subtrees()) {
      traverseVectors(vectors, source, subTree);
    }
    return vectors;
  }

  private static void traverseVectors(List<Entry<String, String>> vectors,
      TreeNode<SimpleOrganizationUnit> parent, TreeNode<SimpleOrganizationUnit> child) {
    vectors.add(new MutablePair<>(parent.data().getOrganizationUnitName(),
        child.data().getOrganizationUnitName()));
    if (!child.isLeaf()) {
      for (var subTree : child.subtrees()) {
        traverseVectors(vectors, child, subTree);
      }
    }
  }

  default Optional<OrganizationTree> findOrganizationTree(OrganizationUnit organizationUnit) {
    return getOrganizationTrees().stream().filter(ot -> ot.isTreeNode(organizationUnit))
        .findFirst();
  }

  default boolean isTreeNode(OrganizationUnit organizationUnit) {
    return getOrganizationTrees().stream().anyMatch(ot -> ot.isTreeNode(organizationUnit));
  }

  default List<SimpleOrganizationUnit> getAllTreeOrganizationUnits(
      OrganizationUnit organizationUnit) {
    var organizationUnits = new ArrayList<SimpleOrganizationUnit>();

    findOrganizationTree(organizationUnit).ifPresent(ot -> {
      if (ot.hasDefaultRoot()) organizationUnits.add(ot.getDefaultRoot());
      for (var treeRepo : getOrganizationUnitRepositories()) {
        if (ot.getNodeTypes().contains(treeRepo.getResourceType())) {
          treeRepo.findAll().forEach(ou -> {
            organizationUnits.add(new SimpleOrganizationUnit(ou));
          });
        }
      }
    });

    return organizationUnits;
  }

}
