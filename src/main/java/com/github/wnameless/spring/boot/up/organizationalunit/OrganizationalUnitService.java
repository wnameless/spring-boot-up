package com.github.wnameless.spring.boot.up.organizationalunit;

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

public interface OrganizationalUnitService<ID> {

  List<? extends OrganizationalUnitRepository<? extends OrganizationalUnit<ID>, ID>> getOrganizationalUnitRepositories();

  Set<OrganizationalChart<ID>> getOrganizationCharts();

  default Optional<? extends OrganizationalUnit<ID>> findOrganizationalUnit(
      ID organizationalUnitId) {
    var rootOpt = getOrganizationCharts().stream().filter(
        ot -> Objects.equals(ot.getDefaultRoot().getOrganizationalUnitId(), organizationalUnitId))
        .findFirst();
    if (rootOpt.isPresent()) return Optional.of(rootOpt.get().getDefaultRoot());

    for (var repo : getOrganizationalUnitRepositories()) {
      var ouOpt = repo.findByOrganizationalUnitId(organizationalUnitId);
      if (ouOpt.isPresent()) return ouOpt;
    }
    return Optional.empty();
  }

  default Optional<? extends OrganizationalUnit<ID>> findOrganizationalUnit(ID organizationUnitId,
      Class<?> organizationUnitType) {
    if (SimpleOrganizationalUnit.class.isAssignableFrom(organizationUnitType)) {
      var rootOpt = getOrganizationCharts().stream().filter(
          ot -> Objects.equals(ot.getDefaultRoot().getOrganizationalUnitId(), organizationUnitId))
          .findFirst();
      if (rootOpt.isPresent()) return Optional.of(rootOpt.get().getDefaultRoot());
    }

    for (var repo : getOrganizationalUnitRepositories()) {
      var ouOpt = repo.findByOrganizationalUnitId(organizationUnitId);
      if (ouOpt.isPresent()) {
        if (organizationUnitType.isAssignableFrom(ouOpt.get().getClass())) {
          return ouOpt;
        }
      }
    }
    return Optional.empty();
  }

  default TreeNode<SimpleOrganizationalUnit<ID>> toTreeNode(ID organizationUnitId) {
    var ouOpt = findOrganizationalUnit(organizationUnitId);
    if (ouOpt.isEmpty()) return null;
    if (!(isTreeNode(ouOpt.get()))) return null;
    return toTreeNode(ouOpt.get());
  }

  default TreeNode<SimpleOrganizationalUnit<ID>> toTreeNode(
      OrganizationalUnit<ID> organizationUnit) {
    if (organizationUnit == null) return null;

    var oues = getAllTreeOrganizationalUnits(organizationUnit);
    var rootOpt =
        oues.stream().filter(ou -> ou.getParentOrganizationalUnitId() == null).findFirst();
    if (rootOpt.isEmpty()) return null;

    var root = rootOpt.get();
    oues.remove(root);
    var rootNode = new ArrayMultiTreeNode<SimpleOrganizationalUnit<ID>>(
        new SimpleOrganizationalUnit<ID>(root));

    List<TreeNode<SimpleOrganizationalUnit<ID>>> leaves = new ArrayList<>(Arrays.asList(rootNode));
    do {
      leaves = buildTree(leaves, oues);
    } while (!leaves.isEmpty());

    return rootNode;
  }

  private static <ID> List<TreeNode<SimpleOrganizationalUnit<ID>>> buildTree(
      List<TreeNode<SimpleOrganizationalUnit<ID>>> leaves,
      List<SimpleOrganizationalUnit<ID>> simpleOrganizationUnits) {
    var newLeaves = new ArrayList<TreeNode<SimpleOrganizationalUnit<ID>>>();

    for (var leaf : leaves) {
      var targets = simpleOrganizationUnits.stream().filter(sou -> Objects
          .equals(leaf.data().getOrganizationalUnitId(), sou.getParentOrganizationalUnitId()))
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

  default List<Entry<String, String>> toNodeVectors(TreeNode<SimpleOrganizationalUnit<ID>> source) {
    List<Entry<String, String>> vectors = new ArrayList<>();
    vectors.add(new MutablePair<>(source.data().getOrganizationalUnitName(),
        source.data().getOrganizationalUnitName()));
    for (var subTree : source.subtrees()) {
      traverseVectors(vectors, source, subTree);
    }
    return vectors;
  }

  private static <ID> void traverseVectors(List<Entry<String, String>> vectors,
      TreeNode<SimpleOrganizationalUnit<ID>> parent, TreeNode<SimpleOrganizationalUnit<ID>> child) {
    vectors.add(new MutablePair<>(parent.data().getOrganizationalUnitName(),
        child.data().getOrganizationalUnitName()));
    if (!child.isLeaf()) {
      for (var subTree : child.subtrees()) {
        traverseVectors(vectors, child, subTree);
      }
    }
  }

  default Optional<OrganizationalChart<ID>> findOrganizationalTree(
      OrganizationalUnit<ID> organizationUnit) {
    return getOrganizationCharts().stream().filter(ot -> ot.isTreeNode(organizationUnit))
        .findFirst();
  }

  default boolean isTreeNode(OrganizationalUnit<ID> organizationUnit) {
    return getOrganizationCharts().stream().anyMatch(ot -> ot.isTreeNode(organizationUnit));
  }

  default List<SimpleOrganizationalUnit<ID>> getAllTreeOrganizationalUnits(
      OrganizationalUnit<ID> organizationUnit) {
    var organizationUnits = new ArrayList<SimpleOrganizationalUnit<ID>>();

    findOrganizationalTree(organizationUnit).ifPresent(ot -> {
      if (ot.hasDefaultRoot()) organizationUnits.add(ot.getDefaultRoot());
      for (var treeRepo : getOrganizationalUnitRepositories()) {
        if (ot.getNodeTypes().contains(treeRepo.getResourceType())) {
          treeRepo.findAll().forEach(ou -> {
            organizationUnits.add(new SimpleOrganizationalUnit<ID>(ou));
          });
        }
      }
    });

    return organizationUnits;
  }

}
