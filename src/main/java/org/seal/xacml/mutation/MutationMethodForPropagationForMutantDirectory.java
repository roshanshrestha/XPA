package org.seal.xacml.mutation;

import java.util.HashMap;
import java.util.Map;

public class MutationMethodForPropagationForMutantDirectory {
	 private static Map<String,String> map;
	 static {
		 map = new HashMap<String,String>();
		 map.put("createPolicyTargetTrueMutants", "SELF");
		 map.put("createPolicyTargetFalseMutants", "SELF");
		 map.put("createCombiningAlgorithmMutants", "SELF");
		 map.put("createRuleEffectFlippingMutants", "SELF");
		 map.put("createRuleTargetTrueMutants", "SELF");
		 map.put("createRuleTargetFalseMutants", "createRemoveDefaultRulesMutant");
		 map.put("createRuleConditionTrueMutants", "SELF");
		 map.put("createRuleConditionFalseMutants", "createRemoveDefaultRulesMutant");
		 map.put("createAddNotFunctionMutants", "createRemoveDefaultRulesMutant");
		 map.put("createRemoveNotFunctionMutants", "createRemoveDefaultRulesMutant");
		 map.put("createFirstPermitRuleMutants", "SELF");
		 map.put("createFirstDenyRuleMutants", "SELF");
		 map.put("createRemoveRuleMutants", "createRemoveDefaultRulesMutant");
		 map.put("createRuleChangeComparisonFunctionMutants", "SELF");
		 map.put("createPolicyTargetChangeComparisonFunctionMutants", "SELF");
		 map.put("createAddNewRuleMutants", "createRemoveDefaultRulesMutant");
		 map.put("createRemoveDefaultRulesMutant", "SELF");
		 map.put("createRemoveParallelTargetElementMutants", "SELF");
		 map.put("createCombiningAlgorithmMutants", "SELF");
			
	 }
	 
	 public static String getMutationMethod(String method){
		 return map.get(method);
	 }
}
