package com.suntecgroup.custom.processor.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.suntecgroup.custom.processor.model.smartconnector.SmartMapping;


public class SmartMappingUtils {

	public static List<Map<String, Object>> inisiateRootDenorm(Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, Map<String, Object> processMap, List<String> rootDenorMainArrayLst,
			List<SmartMapping> priAttrList, Map<String, List<SmartMapping>> denormMap) throws Exception {
		List<Map<String, Object>> responseObj = new ArrayList<>();
		List<Map<String, Object>> attrArray = new ArrayList<>();
		List<Map<String, Object>> resultOutDenormLst = new ArrayList<>();
		List<Map<String, Object>> mainArrayDenormLst = new ArrayList<>();
		List<SmartMapping> mappingFinal = new ArrayList<>();
		Map<String, Object> primitiveOutput = new HashMap<>();

		// Get Primary output
		primitiveOutput = constructPriRecord(inputBERecord, priAttrList, processMap);
		// get first array name
		for (String denormArrayObj : rootDenorMainArrayLst) {
			// get first array name value from content result list
			attrArray = fetchRootArrayValue(denormArrayObj, inputBERecord);
			for (Map.Entry<String, List<SmartMapping>> denormMapObj : denormMap.entrySet()) {
				if (denormArrayObj.equalsIgnoreCase(denormMapObj.getKey())) {
					mappingFinal = denormMapObj.getValue();

				}
			}
			resultOutDenormLst = constructArrayRes(attrArray, mappingFinal, processMap, denormMap);
			if (mainArrayDenormLst.isEmpty()) {
				mainArrayDenormLst.addAll(resultOutDenormLst);
			} else {
				mainArrayDenormLst = mergeMainArrList(mainArrayDenormLst, resultOutDenormLst);
			}
		}
		if (mainArrayDenormLst.isEmpty()) {
			responseObj.add(primitiveOutput);
		} else {
			responseObj = mergePriArrayAttr(primitiveOutput, mainArrayDenormLst);
		}

		return responseObj;
	}
	
	public static List<Map<String, Object>> fetchRootArrayValue(String denormArrayObj,
			Map<String, Object> inputBERecord) {
		List<Map<String, Object>> attrArray = new ArrayList<Map<String, Object>>();
		Iterator<Entry<String, Object>> mapIterator = inputBERecord.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
			if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(denormArrayObj)) {
				attrArray = (List) ev.getValue();
				break;
			} else if (null != ev.getKey() && ev.getValue() instanceof Map) {
				Map<String, Object> inputObj = (Map) ev.getValue();
				attrArray = fetchRootArrayValue(denormArrayObj, inputObj);
				if (null != attrArray) {
					if (attrArray.size() > 0) {
						break;
					}
				}
			}
		}

		return attrArray;
	}

	public static List<Map<String, Object>> mergeMainArrList(List<Map<String, Object>> mainArrayDenormLst,
			List<Map<String, Object>> resultOutDenormLst) {
		List<Map<String, Object>> outputBeRecordLst = new ArrayList<>();
		if (resultOutDenormLst.size() == 0) {
			return mainArrayDenormLst;
		}
		for (Map<String, Object> obj1 : resultOutDenormLst) {
			outputBeRecordLst.addAll(mergePriArrayAttr(obj1, mainArrayDenormLst));
		}

		return outputBeRecordLst;
	}

	public static List<Map<String, Object>> mergePriArrayAttr(Map<String, Object> responsePrimitive,
			List<Map<String, Object>> subArrayLst) {
		List<Map<String, Object>> mergeArrayLst = new ArrayList<>();
		for (Map<String, Object> currentrec : subArrayLst) {
			Map<String, Object> currentNewRec = new HashMap<String, Object>();
			currentNewRec.putAll(currentrec);
			currentNewRec.putAll(responsePrimitive);
			mergeArrayLst.add(currentNewRec);
		}
		return mergeArrayLst;
	}

	public static List<Map<String, Object>> constructArrayRes(List<Map<String, Object>> attrArray,
			List<SmartMapping> mappingFinal, Map<String, Object> processMap, Map<String, List<SmartMapping>> denormMap) throws Exception {
		List<Map<String, Object>> responseobj = new ArrayList<>();
		List<Map<String, Object>> mappedArrayRec = new ArrayList<>();
		for (Map<String, Object> inputRecord : attrArray) {
			mappedArrayRec = constructArrayRecord(inputRecord, mappingFinal, processMap, denormMap);
			responseobj.addAll(mappedArrayRec);
		}
		return responseobj;
	}

	public static List<Map<String, Object>> constructArrayRecord(Map<String, Object> inputRecord,
			List<SmartMapping> mappingFinal, Map<String, Object> processMap, Map<String, List<SmartMapping>> denormMap)
			throws Exception {
		List<Map<String, Object>> responseArrayRecord = new ArrayList<>();
		List<Map<String, Object>> subArrayLst = new ArrayList<>();
		List<Map<String, Object>> attrSubArray = new ArrayList<>();
		Map<String, Object> responsePrimitive = new HashMap<>();
		for (SmartMapping mappingObj : mappingFinal) {
			if (!mappingObj.getDataType().equalsIgnoreCase("array")) {
				String key = mappingObj.getToValue();
				String value = fetchPrimitiveValue(mappingObj.getFromValue(), mappingObj.getFromPath().split("-"),
						inputRecord);
				if (value != null && mappingObj.getTypeConversionArray() != null) {
					value = CommonUtils.typeConvertResult(value, mappingObj.getTypeConversionArray()).toString();
				}
				responsePrimitive.put(key, value);
			} else if (mappingObj.getDataType().equalsIgnoreCase("array")) {
				for (Map.Entry<String, List<SmartMapping>> denormMapObj : denormMap.entrySet()) {
					if (mappingObj.getFromValue().equalsIgnoreCase(denormMapObj.getKey())) {
						mappingFinal = denormMapObj.getValue();
					}
					Iterator<Entry<String, Object>> mapIterator = inputRecord.entrySet().iterator();
					while (mapIterator.hasNext()) {
						Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();

						if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(mappingObj.getFromValue())) {
							attrSubArray = (List) ev.getValue();
							break;
						}
					}
					subArrayLst = constructArrayRes(attrSubArray, mappingFinal, processMap, denormMap);
				}
			}
		}
		if (subArrayLst.isEmpty()) {
			responseArrayRecord.add(responsePrimitive);
		} else {
			responseArrayRecord = mergePriArrayAttr(responsePrimitive, subArrayLst);
		}
		return responseArrayRecord;
	}

	public static Map<String, Object> constructPriRecord(Map<String, Object> inputBERecord, List<SmartMapping> priAttrList,
			Map<String, Object> processMap) {
		Map<String, Object> responseObj = new HashMap<>();
		for (SmartMapping obj : priAttrList) {
			String key = obj.getToValue();
			String value = fetchPrimitiveValue(obj.getFromValue(), obj.getFromPath().split("-"), inputBERecord);
			responseObj.put(key, value);
		}

		return responseObj;
	}

	public static String fetchPrimitiveValue(String attrName, String[] fromPath, Map<String, Object> inputBERecord) {
		String attrValue = "";
		Iterator<Entry<String, Object>> mapIterator = inputBERecord.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
			int attrPos = 2;
			String root = fromPath[fromPath.length - attrPos];
			if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(attrName)) {
				attrValue = ev.getValue().toString();
				if (attrValue != null) {
					return attrValue;
				}
			} else if (ev.getKey().equalsIgnoreCase(root)) {
				Map<String, Object> rootObject = (Map<String, Object>) ev.getValue();
				attrValue = fetchSubObject(fromPath, attrName, rootObject, attrPos);
				if (attrValue != null) {
					return attrValue;
				}
			}
		}

		return attrValue;
	}

	public static String fetchSubObject(String[] fromPath, String fieldName, Map<String, Object> rootObject,
			int attrPos) {
		String value = "";
		Iterator<Entry<String, Object>> mapIterator = rootObject.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
			int subAttrPos = attrPos + 1;
			String root = fromPath[fromPath.length - subAttrPos];
			if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(fieldName)) {
				value = ev.getValue().toString();
				if (value != null) {
					return value;
				}
			} else if (ev.getKey().equalsIgnoreCase(root)) {
				Map<String, Object> rootsubObject = (Map<String, Object>) ev.getValue();
				value = fetchSubObject(fromPath, fieldName, rootsubObject, subAttrPos);
				if (value != null) {
					return value;
				}
			}
		}

		return value;
	}

	public static Map<String, List<SmartMapping>> fetch(List<String> rootDenorMainArrayLst, List<SmartMapping> mappingList) {
		int size;
		Map<String, List<SmartMapping>> responseObj = new HashMap<>();
		for (String obj : rootDenorMainArrayLst) {
			size = 1;
			List<SmartMapping> value = new ArrayList<>();
			do {
				for (SmartMapping mapObj : mappingList) {
					String[] fromPath = mapObj.getFromPath().split("-");
					int fromPathLength = fromPath.length;
					if (fromPathLength == size + 2) {
						List<String> list = Arrays.asList(fromPath);
						Collections.reverse(list);
						String[] fromPathReverse = (String[]) list.toArray();
	
						if (obj.equalsIgnoreCase(fromPathReverse[size]) && mapObj.getDataType().equalsIgnoreCase("array")) {
							fetchSubArray(mapObj.getFromValue(), mappingList, size + 1, responseObj);
							value.add(mapObj);
						} else if (obj.equalsIgnoreCase(fromPathReverse[size])) {
							value.add(mapObj);
						}
					}
				}
				size++;
			} while (value.size() == 0 && size < 20);
			responseObj.put(obj, value);
		}
		return responseObj;
	}

	public static List<SmartMapping> fetchSubArray(String subArrayAttr, List<SmartMapping> mappingList, int sizeSub,
			Map<String, List<SmartMapping>> responseObj) {

		List<SmartMapping> subArrayObjLst = new ArrayList<>();
		for (SmartMapping mapSubObj : mappingList) {

			String[] frompathSub = mapSubObj.getFromPath().split("-");
			int fromPathLengthSub = frompathSub.length;
			if (fromPathLengthSub == sizeSub + 2) {
				List<String> list = Arrays.asList(frompathSub);
				Collections.reverse(list);
				String[] fromPathReverse = (String[]) list.toArray();
				if (subArrayAttr.equalsIgnoreCase(fromPathReverse[sizeSub])
						&& mapSubObj.getDataType().equalsIgnoreCase("array")) {
					fetchSubArray(subArrayAttr, mappingList, sizeSub + 1, responseObj);
					subArrayObjLst.add(mapSubObj);
				} else if (subArrayAttr.equalsIgnoreCase(fromPathReverse[sizeSub])) {
					subArrayObjLst.add(mapSubObj);
				}
			}
		}
		responseObj.put(subArrayAttr, subArrayObjLst);

		return subArrayObjLst;
	}

	public static List<Map<String, Object>> getSubArayDenorm(Map<String, Object> inputBERecord, List<SmartMapping> mappingList,
			String toCurrent) throws Exception {

		List<String> arrayMapping = new ArrayList<String>();
		List<String> resultArrayMapping = new ArrayList<String>();
		boolean found = false;
		String mapPath, resultElem;
		for (SmartMapping map : mappingList) {
			if (map.getToValue().equalsIgnoreCase(toCurrent)) {
				found = false;
				for (int j = 0; j < arrayMapping.size(); j++) {
					mapPath = map.getFromPath();
					resultElem = arrayMapping.get(j);
					if (mapPath.split("-").length < resultElem.split("-").length) {
						if (mapPath.length() <= resultElem.length()) {
							if (resultElem.contains(mapPath)) {
								found = true;
								arrayMapping.set(j, map.getFromPath());
								resultArrayMapping.set(j, map.getFromValue());
							}
						}
					} else {
						if (resultElem.length() <= mapPath.length()) {
							if (mapPath.contains(resultElem)) {
								found = true;
							}
						}
					}
				}
				if (!found) {
					arrayMapping.add(map.getFromPath());
					resultArrayMapping.add(map.getFromValue());
				}
			}
		}

		Map<String, List<SmartMapping>> denormMap = SmartMappingUtils.fetch(resultArrayMapping, mappingList);
		List<Map<String, Object>> response = initiateSubDenorm(inputBERecord, resultArrayMapping, denormMap);
		return response;
	}

	public static List<Map<String, Object>> initiateSubDenorm(Map<String, Object> inputBERecord,
			List<String> arrayMapping, Map<String, List<SmartMapping>> denormMap) throws Exception {
		List<Map<String, Object>> attrArray = new ArrayList<>();
		List<SmartMapping> mappingFinal = new ArrayList<>();
		List<Map<String, Object>> resultOutDenormLst = new ArrayList<>();
		List<Map<String, Object>> mainArrayDenormLst = new ArrayList<>();
		for (String subDenormArrayObj : arrayMapping) {
			attrArray = fetchRootArrayValue(subDenormArrayObj, inputBERecord);
			for (Map.Entry<String, List<SmartMapping>> denormMapObj : denormMap.entrySet()) {
				if (subDenormArrayObj.equalsIgnoreCase(denormMapObj.getKey())) {
					mappingFinal = denormMapObj.getValue();
				}
			}
			resultOutDenormLst = constructArrayRes(attrArray, mappingFinal, null, denormMap);
			if (mainArrayDenormLst.isEmpty()) {
				mainArrayDenormLst.addAll(resultOutDenormLst);
			} else {
				mainArrayDenormLst = mergeMainArrList(mainArrayDenormLst, resultOutDenormLst);
			}
		}
		return mainArrayDenormLst;
	}
}
