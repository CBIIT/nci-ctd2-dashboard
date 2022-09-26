package gov.nih.nci.ctd2.dashboard.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.Hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Controller
@RequestMapping("/tree")
public class TreeController {
    private static final Log log = LogFactory.getLog(TreeController.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "disease-context", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getDiseaseContextTree() {
        log.debug("request received by getDiseaseContextTree ");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        System.out.println("tree summary: " + Hierarchy.DISEASE_CONTEXT);
        // flattenDiseaseContextTree(); // TODO for 'observed' subjects
        System.out.println("number of top level nodes=" + Hierarchy.DISEASE_CONTEXT.topNodes());

        Map<Integer, int[]> map = Hierarchy.DISEASE_CONTEXT.map();
        List<Node> nodes = new ArrayList<Node>();
        for (Integer key : map.keySet()) {
            int[] id_children = map.get(key);
            List<Node> children = new ArrayList<Node>();
            for (int child : id_children) {
                children.add(new Node(String.valueOf(child)));
            }
            addNode(nodes, new Node(String.valueOf(key), children));
        }
        Node tree = new Node("root", nodes);
        log.debug("top level: " + nodes.size());
        log.debug("total nodes: " + tree.totalSize());

        JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "evidence-type", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getEvidenceTypeTree() {
        log.debug("request received by getEvidenceTypeTree ");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        System.out.println("tree summary: " + Hierarchy.EXPERIMENTAL_EVIDENCE);
        System.out.println("number of top level nodes=" + Hierarchy.EXPERIMENTAL_EVIDENCE.topNodes());

        Map<Integer, int[]> map = Hierarchy.EXPERIMENTAL_EVIDENCE.map();
        List<Node> nodes = new ArrayList<Node>();
        for (Integer key : map.keySet()) {
            int[] id_children = map.get(key);
            List<Node> children = new ArrayList<Node>();
            for (int child : id_children) {
                children.add(new Node(String.valueOf(child)));
            }
            addNode(nodes, new Node(String.valueOf(key), children));
        }
        Node tree = new Node("root", nodes);
        log.debug("top level: " + nodes.size());
        log.debug("total nodes: " + tree.totalSize());

        JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    static private void addNode(final List<Node> nodes, Node newNode) {
        List<Node> to_be_remove = new ArrayList<Node>();
        for (Node node : nodes) {
            if (newNode.addChild(node)) {
                to_be_remove.add(node);
            }
        }
        for (Node x : to_be_remove) {
            nodes.remove(x);
        }
        for (Node node : nodes) {
            if (node.addChild(newNode)) {
                return;
            }
        }
        nodes.add(newNode);
    }

    public static class Node {
        public final String name;
        public final List<Node> children;

        public Node(String name, List<Node> children) {
            this.name = name;
            this.children = children;
        }

        public Node(String name) {
            this.name = name;
            this.children = new ArrayList<Node>();
        }

        public boolean addChild(Node node) {
            for (Node child : children) {
                if (child.name.equals(node.name)) {
                    child.children.addAll(node.children);
                    return true;
                } else if (child.addChild(node)) {
                    return true;
                }
            }
            return false;
        }

        /* total number of nodes, include this node itself and all the decedents */
        public int totalSize() {
            int i = 1;
            for (Node x : children) {
                i += x.totalSize();
            }
            return i;
        }
    }
}