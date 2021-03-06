package org.seal.xacml.coverage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.seal.xacml.NameDirectory;
import org.seal.xacml.RequestGeneratorBase;
import org.seal.xacml.utils.RequestBuilder;
import org.seal.xacml.utils.XACMLElementUtil;
import org.seal.xacml.utils.XMLUtil;
import org.seal.xacml.utils.Z3StrUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.ParsingException;
import org.wso2.balana.Rule;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.xacml3.Target;
import org.xml.sax.SAXException;

/**
 * Created by roshanshrestha on 2/10/17.
 */
public class RuleCoverage extends RequestGeneratorBase {
	private boolean conditionFlag;
	private boolean targetFlag;
	
	public RuleCoverage(String policyFilePath) throws ParsingException, IOException, SAXException, ParserConfigurationException{
		init(policyFilePath);
	}
	
	public List<String> generateRequests() throws ParsingException, IOException, SAXException, ParserConfigurationException{
		conditionFlag = targetFlag = true;
        StringBuilder preExpression = new StringBuilder();
        
        traverse( doc.getDocumentElement(), preExpression,null,false);
        return getRequests();
    }
	
	public List<String> generateRequestsForTruthValues(boolean targetFlag, boolean conditionFlag, boolean falsifyPostRules) throws ParsingException, IOException, SAXException, ParserConfigurationException{
		this.targetFlag = targetFlag;
		this.conditionFlag = conditionFlag;
        StringBuilder preExpression = new StringBuilder();
        List<String> paths = new ArrayList<String>();
        traverse( doc.getDocumentElement(), preExpression,null, falsifyPostRules);
        return getRequests();
    }
    
   private void traverse(Element node, StringBuilder preExpression, List<Rule> previousRules, boolean falsifyPostRules) throws ParsingException, IOException {
	    if (XACMLElementUtil.isRule(node)) {
	    	String expression;
	    	if(falsifyPostRules){
	    		expression = getRuleExpressionForTruthValuesWithPostRules(node,preExpression,previousRules);
	    	} else{
	    		expression = getRuleCoverageExpression(node,preExpression,previousRules);
	    	}
			boolean sat = Z3StrUtil.processExpression(expression, z3ExpressionHelper);
			if (sat == true) {
			    addRequest(RequestBuilder.buildRequest(z3ExpressionHelper.getAttributeList()));
			}
		    previousRules.add(Rule.getInstance(node, policyMetaData, null));
		    return;
		}
		
		if (XACMLElementUtil.isPolicy(node) || XACMLElementUtil.isPolicySet(node)) {
		    Node targetNode = XMLUtil.findInChildNodes(node, NameDirectory.TARGET);
		    Target target;
			if (targetNode != null) {
		        target = Target.getInstance(targetNode, policyMetaData);
		        if(target.getAnyOfSelections().size()>0){
		        	preExpression.append(z3ExpressionHelper.getTrueTargetExpression(target) + System.lineSeparator());
		        }
		    }
	        NodeList children = node.getChildNodes();
	        previousRules = null;
	        if(XACMLElementUtil.isPolicy(node)){
	        	previousRules = new ArrayList<Rule>();
	        }
	        for (int i = 0; i < children.getLength(); i++) {
	            Node child = children.item(i);
	            StringBuilder preExpressionCurrent = new StringBuilder(preExpression.toString());
	            if (child instanceof Element && XMLUtil.isTraversableElement(child)) {
	            	traverse((Element) child, preExpressionCurrent, previousRules,falsifyPostRules);
	            }
	        }
		}	
    }

	public String getRuleCoverageExpression (Element node, StringBuilder preExpression, List<Rule> previousRules) throws ParsingException{
		return getRuleExpressionForTruthValues(node, preExpression, previousRules);
	}
	
	public String getRuleExpressionForTruthValues(Element node, StringBuilder preExpression, List<Rule> previousRules) throws ParsingException{
	    Target target = XMLUtil.getTarget(node, policyMetaData);
	    Condition condition = XMLUtil.getCondition(node, policyMetaData);
	    StringBuffer ruleExpression = new StringBuffer();
	    if(target != null){
		    if(targetFlag){
		    	ruleExpression.append(z3ExpressionHelper.getTrueTargetExpression(target) + System.lineSeparator());
		    }else{
		    	ruleExpression.append(z3ExpressionHelper.getFalseTargetExpression(target) + System.lineSeparator());
		    }
	    }
	    if(condition != null){
	    	if(conditionFlag){
	    		ruleExpression.append(z3ExpressionHelper.getTrueConditionExpression(condition) + System.lineSeparator());
	    	} else{
	    		ruleExpression.append(z3ExpressionHelper.getFalseConditionExpression(condition) + System.lineSeparator());
	    	}
	    }
	    StringBuffer falsifyPreviousRules = new StringBuffer();
	    for(Rule rule:previousRules){
			falsifyPreviousRules.append(z3ExpressionHelper.getFalseTargetFalseConditionExpression(rule) + System.lineSeparator());
		}                
	    return preExpression.toString()+ruleExpression+falsifyPreviousRules;
	}
	
	public String getRuleExpressionForTruthValuesWithPostRules(Element node, StringBuilder preExpression, List<Rule> previousRules) throws ParsingException{
		String expression =  getRuleExpressionForTruthValues(node, preExpression, previousRules);
		List<Rule> postRules = new ArrayList<Rule>();
		Node sibling = null;
		Node n = node;
		while(true){
			sibling = n.getNextSibling();
			if(sibling == null){
				break;
			} else{
				if(sibling.getNodeType() == Node.ELEMENT_NODE){
					if(XACMLElementUtil.isRule(sibling)){
						postRules.add(Rule.getInstance(sibling, policyMetaData, null));
					}
				} 
			}
			n = sibling;
		}
		StringBuffer falsifyPostRules = new StringBuffer();
	    for(Rule rule:postRules){
	    	falsifyPostRules.append(z3ExpressionHelper.getFalseTargetFalseConditionExpression(rule) + System.lineSeparator());
		}
		return expression + falsifyPostRules;
	}
	
	
}
