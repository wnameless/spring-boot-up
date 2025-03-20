package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

  default List<? extends OrganizationalUnit<ID>> findAllOrganizationalUnitsByResourceTypes(
      Collection<Class<? extends OrganizationalUnit<ID>>> resourceTypes) {
    List<OrganizationalUnit<ID>> result = new ArrayList<>();

    getOrganizationalUnitRepositories().forEach(repo -> {
      if (resourceTypes.contains(repo.getResourceType())) {
        repo.findAll().forEach(ou -> result.add(ou));
      }
    });

    return result;
  }

  default List<? extends OrganizationalUnit<ID>> findAllOrganizationalUnits() {
    List<OrganizationalUnit<ID>> result = new ArrayList<>();

    getOrganizationalUnitRepositories().forEach(repo -> {
      repo.findAll().forEach(ou -> result.add(ou));
    });

    return result;
  }

  default List<Optional<? extends OrganizationalUnit<ID>>> findAllOrganizationalUnits(
      Collection<ID> organizationalUnitIds) {
    List<Optional<? extends OrganizationalUnit<ID>>> result = new ArrayList<>();
    if (organizationalUnitIds.isEmpty()) return result;
    Map<ID, OrganizationalUnit<ID>> map = new LinkedHashMap<>();

    for (var repo : getOrganizationalUnitRepositories()) {
      repo.findAllByOrganizationalUnitIds(organizationalUnitIds).forEach(ou -> {
        map.put(ou.getOrganizationalUnitId(), ou);
      });
    }

    organizationalUnitIds.forEach(id -> {
      if (map.containsKey(id)) {
        result.add(Optional.of(map.get(id)));
      } else {
        result.add(Optional.empty());
      }
    });
    return result;
  }

  default Optional<? extends OrganizationalUnit<ID>> findOrganizationalUnit(
      ID organizationalUnitId) {
    if (organizationalUnitId == null) return Optional.empty();

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

  default Optional<? extends OrganizationalUnit<ID>> findOrganizationalUnit(ID organizationalUnitId,
      Class<?> organizationalUnitType) {
    if (organizationalUnitId == null) return Optional.empty();

    if (SimpleOrganizationalUnit.class.isAssignableFrom(organizationalUnitType)) {
      var rootOpt = getOrganizationCharts().stream().filter(
          ot -> Objects.equals(ot.getDefaultRoot().getOrganizationalUnitId(), organizationalUnitId))
          .findFirst();
      if (rootOpt.isPresent()) return Optional.of(rootOpt.get().getDefaultRoot());
    }

    for (var repo : getOrganizationalUnitRepositories()) {
      var ouOpt = repo.findByOrganizationalUnitId(organizationalUnitId);
      if (ouOpt.isPresent()) {
        if (organizationalUnitType.isAssignableFrom(ouOpt.get().getClass())) {
          return ouOpt;
        }
      }
    }

    return Optional.empty();
  }

  default TreeNode<SimpleOrganizationalUnit<ID>> toTreeNode(ID organizationalUnitId) {
    var ouOpt = findOrganizationalUnit(organizationalUnitId);
    if (ouOpt.isEmpty()) return null;
    if (!(isTreeNode(ouOpt.get()))) return null;
    return toTreeNode(ouOpt.get());
  }

  default TreeNode<SimpleOrganizationalUnit<ID>> toTreeNode(
      OrganizationalUnit<ID> organizationalUnit) {
    if (organizationalUnit == null) return null;

    var oues = getAllTreeOrganizationalUnits(organizationalUnit);
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
      List<SimpleOrganizationalUnit<ID>> simpleOrganizationalUnits) {
    var newLeaves = new ArrayList<TreeNode<SimpleOrganizationalUnit<ID>>>();

    for (var leaf : leaves) {
      var targets = simpleOrganizationalUnits.stream().filter(sou -> Objects
          .equals(leaf.data().getOrganizationalUnitId(), sou.getParentOrganizationalUnitId()))
          .toList();
      simpleOrganizationalUnits.removeAll(targets);
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
      OrganizationalUnit<ID> organizationalUnit) {
    if (organizationalUnit == null) return Optional.empty();

    return getOrganizationCharts().stream().filter(ot -> ot.isTreeNode(organizationalUnit))
        .findFirst();
  }

  default boolean isTreeNode(OrganizationalUnit<ID> organizationalUnit) {
    return getOrganizationCharts().stream().anyMatch(ot -> ot.isTreeNode(organizationalUnit));
  }

  default List<SimpleOrganizationalUnit<ID>> getAllTreeOrganizationalUnits(
      OrganizationalUnit<ID> organizationalUnit) {
    var organizationalUnits = new ArrayList<SimpleOrganizationalUnit<ID>>();

    findOrganizationalTree(organizationalUnit).ifPresent(ot -> {
      if (ot.hasDefaultRoot()) organizationalUnits.add(ot.getDefaultRoot());
      for (var treeRepo : getOrganizationalUnitRepositories()) {
        if (ot.getNodeTypes().contains(treeRepo.getResourceType())) {
          treeRepo.findAll().forEach(ou -> {
            organizationalUnits.add(new SimpleOrganizationalUnit<ID>(ou));
          });
        }
      }
    });

    return organizationalUnits;
  }

  default List<? extends OrganizationalUnit<ID>> findAllOrganizationalUnits(
      String organizationalUnitName) {
    List<OrganizationalUnit<ID>> result = new ArrayList<>();

    for (var repo : getOrganizationalUnitRepositories()) {
      var oues = repo.findAllByOrganizationalUnitName(organizationalUnitName);
      result.addAll(oues);
    }

    return result;
  }

}
