package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

public interface OrganizationUnitService<ID> {

  List<? extends OrganizationUnitRepository<? extends OrganizationUnit, ID>> getOrganizationUnitRepositories();

  Map<Set<Class<?>>, List<OrganizationUnitRepository<? extends OrganizationUnit, ID>>> getOrganizationTreeableRepositories();

  Map<Set<Class<?>>, OrganizationUnit> getRootOrganizationUnits();

  default boolean isTree(OrganizationUnit organizationUnit) {
    return getOrganizationTreeableRepositories().keySet().stream()
        .anyMatch(s -> s.contains(organizationUnit.getClass()))
        || getRootOrganizationUnits().values().contains(organizationUnit);
  }

  Set<? extends OrganizationUnit> getDefaultOrganizationUnits();

  default Optional<? extends OrganizationUnit> findOrganizationUnit(String organizationUnitName) {
    var defaultOuOpt = getDefaultOrganizationUnits().stream()
        .filter(ou -> Objects.equals(ou.getOrganizationUnitName(), organizationUnitName))
        .findFirst();
    if (defaultOuOpt.isPresent()) return defaultOuOpt;

    for (var repo : getOrganizationUnitRepositories()) {
      var ouOpt = repo.findByOrganizationUnitName(organizationUnitName);
      if (ouOpt.isPresent()) return ouOpt;
    }
    return Optional.empty();
  }

  default Optional<? extends OrganizationUnit> findOrganizationUnit(String organizationUnitName,
      Class<?> organizationUnitType) {
    var defaultOuOpt = getDefaultOrganizationUnits().stream()
        .filter(ou -> Objects.equals(ou.getOrganizationUnitName(), organizationUnitName))
        .findFirst();
    if (defaultOuOpt.isPresent()) {
      if (organizationUnitType.isAssignableFrom(defaultOuOpt.get().getClass())) {
        return defaultOuOpt;
      }
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

  default TreeNode<SimpleOrganizationUnit> toTreeNode(String organizationUnitName) {
    var ouOpt = findOrganizationUnit(organizationUnitName);
    if (ouOpt.isEmpty()) return null;
    if (!(isTree(ouOpt.get()))) return null;

    var oues = getAllTreeOrganizationUnits(ouOpt.get());
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

  default List<TreeNode<SimpleOrganizationUnit>> buildTree(
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

  default List<SimpleOrganizationUnit> getAllTreeOrganizationUnits(
      OrganizationUnit organizationUnit) {
    var organizationUnits = new ArrayList<SimpleOrganizationUnit>();
    for (var repo : getTreeRepositories(organizationUnit)) {
      var oues = repo.findAllProjectedBy("organizationUnitName", "parentOrganizationUnitName")
          .stream().map(ou -> new SimpleOrganizationUnit(ou)).toList();
      organizationUnits.addAll(oues);
    }
    getRootOrganizationUnits().entrySet().stream()
        .filter(e -> e.getKey().contains(organizationUnit.getClass())).findFirst().ifPresent(e -> {
          organizationUnits.add(new SimpleOrganizationUnit(e.getValue()));
        });
    return organizationUnits;
  }

  default List<OrganizationUnitRepository<? extends OrganizationUnit, ID>> getTreeRepositories(
      OrganizationUnit organizationUnit) {
    for (var set : getOrganizationTreeableRepositories().keySet()) {
      if (set.contains(organizationUnit.getClass())) {
        return getOrganizationTreeableRepositories().get(set);
      }
    }
    return Collections.emptyList();
  }

}
