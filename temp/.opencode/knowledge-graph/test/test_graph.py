import json
import os
import unittest

class TestKnowledgeGraph(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        # Resolve paths relative to this test file
        cls.test_dir = os.path.dirname(os.path.abspath(__file__))
        cls.kg_dir = os.path.dirname(cls.test_dir)
        cls.repo_root = os.path.dirname(cls.kg_dir)
        
        cls.graph_path = os.path.join(cls.kg_dir, 'graph.json')
        cls.chunks_path = os.path.join(cls.kg_dir, 'source-chunks.json')
        
        with open(cls.graph_path, 'r') as f:
            cls.graph = json.load(f)
            
        with open(cls.chunks_path, 'r') as f:
            cls.chunks = json.load(f)
            
        cls.nodes = cls.graph.get('nodes', [])
        cls.edges = cls.graph.get('edges', [])
        cls.node_ids = {n['id'] for n in cls.nodes}

    # --- 1. Structural Validation ---

    def test_unique_node_ids(self):
        seen = set()
        duplicates = []
        for n in self.nodes:
            nid = n.get('id')
            if nid in seen:
                duplicates.append(nid)
            seen.add(nid)
        self.assertFalse(duplicates, f"Duplicate node IDs found: {duplicates}")

    def test_edge_references_exist(self):
        missing_from = []
        missing_to = []
        for e in self.edges:
            if e.get('from') not in self.node_ids:
                missing_from.append(e.get('from'))
            if e.get('to') not in self.node_ids:
                missing_to.append(e.get('to'))
        
        self.assertFalse(missing_from, f"Edges reference missing 'from' nodes: {missing_from}")
        self.assertFalse(missing_to, f"Edges reference missing 'to' nodes: {missing_to}")

    def test_required_fields_by_type(self):
        reqs = {
            "Document": ["id", "type", "title", "path"],
            "Section": ["id", "type", "title", "sectionNumber", "documentId", "source"],
            "Layer": ["id", "type", "name", "description"],
            "ModulePattern": ["id", "type", "name", "pattern", "source"],
            "Practice": ["id", "type", "name", "statement", "classification", "strength", "source"],
            "Rule": ["id", "type", "name", "statement", "scope", "classification", "strength", "source"],
            "AntiPattern": ["id", "type", "name", "statement", "replacementGuidance", "classification", "strength", "source"],
            "Exception": ["id", "type", "name", "statement", "source"],
            "PlatformPattern": ["id", "type", "name", "statement", "classification", "strength", "source"]
        }
        
        errors = []
        for n in self.nodes:
            ntype = n.get("type")
            self.assertIn(ntype, reqs, f"Unknown node type: {ntype} in node {n.get('id')}")
            for r in reqs[ntype]:
                if r not in n:
                    errors.append(f"Node {n.get('id')} ({ntype}) missing field: '{r}'")
        
        self.assertFalse(errors, "Missing required fields:\n" + "\n".join(errors))

    def test_valid_classifications_and_strengths(self):
        valid_classes = {"universal", "kmp_oriented", "project_specific"}
        valid_strengths = {"non_negotiable", "recommended", "default", "caution"}
        
        invalid_classes = []
        invalid_strengths = []
        
        for n in self.nodes:
            cls = n.get("classification")
            if cls and cls not in valid_classes:
                invalid_classes.append(f"{n.get('id')}: {cls}")
            strg = n.get("strength")
            if strg and strg not in valid_strengths:
                invalid_strengths.append(f"{n.get('id')}: {strg}")
                
        self.assertFalse(invalid_classes, f"Invalid classifications: {invalid_classes}")
        self.assertFalse(invalid_strengths, f"Invalid strengths: {invalid_strengths}")

    # --- 2. Source Integrity ---

    def test_source_integrity(self):
        source_blocks = []
        # Collect sources from nodes
        for n in self.nodes:
            src = n.get("source")
            if src:
                source_blocks.append((n.get("id"), src))
        
        # Collect sources from edges
        for i, e in enumerate(self.edges):
            src = e.get("source")
            if src:
                source_blocks.append((f"edge_{i}_{e.get('from')}_to_{e.get('to')}", src))

        errors = []
        for owner, src in source_blocks:
            fpath = src.get("file", "")
            start = src.get("startLine", 0)
            end = src.get("endLine", 0)
            
            if "BEST_PRACTICES.md" in fpath:
                errors.append(f"[{owner}] references deleted BEST_PRACTICES.md")
                continue
                
            full_path = os.path.join(self.repo_root, fpath)
            if not os.path.exists(full_path):
                errors.append(f"[{owner}] references missing file: {fpath}")
                continue
                
            with open(full_path, 'r') as f:
                lines = f.readlines()
                if start < 1 or end > len(lines) or start > end:
                    errors.append(f"[{owner}] has invalid line range {start}-{end} for {fpath} (len={len(lines)})")

        self.assertFalse(errors, "Source integrity errors:\n" + "\n".join(errors))

    # --- 3. Semantic Coverage & Fixes Verification ---

    def test_core_semantic_rules_exist(self):
        expected_rules = [
            "rule.dependency_direction",
            "rule.domain_pure_kotlin",
            "rule.domain_time_provider_only",
            "rule.repository_translates_infrastructure",
            "rule.rethrow_cancellation_exception",
            "rule.mapper_placement",
            "rule.shared_ui_no_hardcoded_strings",
            "rule.top_level_di_start",
            "practice.callback_navigation_decoupling",
            "rule.no_cross_feature_presentation" # The fix we added!
        ]
        for rule_id in expected_rules:
            self.assertIn(rule_id, self.node_ids, f"Core rule/practice missing: {rule_id}")
            
        # Ensure at least one exception and antipattern exist
        self.assertTrue(any(n.get("type") == "Exception" for n in self.nodes), "Missing Exception nodes")
        self.assertTrue(any(n.get("type") == "AntiPattern" for n in self.nodes), "Missing AntiPattern nodes")

    def test_applied_to_edges_exist_for_navigation(self):
        # We fixed these in the previous step
        has_nav_callback = any(
            e.get('from') == 'practice.callback_navigation_decoupling' and e.get('to') == 'layer.navigation' and e.get('type') == 'APPLIES_TO'
            for e in self.edges
        )
        has_nav_leakage = any(
            e.get('from') == 'antipattern.route_leakage' and e.get('to') == 'layer.navigation' and e.get('type') == 'APPLIES_TO'
            for e in self.edges
        )
        self.assertTrue(has_nav_callback, "Missing APPLIES_TO edge: practice.callback_navigation_decoupling -> layer.navigation")
        self.assertTrue(has_nav_leakage, "Missing APPLIES_TO edge: antipattern.route_leakage -> layer.navigation")

    def test_invalid_requires_edge_removed(self):
        # Ensure our fix removed this
        has_invalid_edge = any(
            e.get('from') == 'rule.domain_time_provider_only' and e.get('to') == 'practice.typed_results' and e.get('type') == 'REQUIRES'
            for e in self.edges
        )
        self.assertFalse(has_invalid_edge, "Graph still contains invalid REQUIRES edge from time provider to typed results")


if __name__ == '__main__':
    unittest.main(verbosity=2)
