package com.suntecgroup.nifi.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Connection;
import com.suntecgroup.nifi.frontend.bean.Operators;

public class BPCanvasUtils {

	private static double leftMarginX = 230;
	private static double rightEdgeX = 230;
	private static double marginY = 690;
	private static double defaultXIncrement = 450;
	private static double defaultYIncrement = 220;
	private static boolean alternate = false;

	public static final String generateID() {
		return UUID.randomUUID().toString();
	}

	public static double getXByPosition(int position) {
		double X = leftMarginX + (position % 10) * defaultXIncrement;
		if (rightEdgeX < X) {
			rightEdgeX = X;
		}
		return X;
	}

	public static double getYByPosition(int position) {
		int index = position / 10;
		double Y = marginY + (index * defaultYIncrement);
		if (alternate) {
			Y = Y + 40;
		} else {
			Y = Y - 40;
		}
		return Y;
	}

	public static void resetPos() {
		leftMarginX = 230;
		rightEdgeX = 230;
		marginY = 690;
		defaultXIncrement = 450;
		defaultYIncrement = 220;
	}

	public static void updateCanvasXMargin() {
		if (leftMarginX != rightEdgeX) {
			leftMarginX = rightEdgeX + 350;
		}
		alternate = !alternate;
	}

	public static void rearrangeOperators(BPFlowUI bpFlowRequest) throws ArrayIndexOutOfBoundsException {
		List<Connection> conn = bpFlowRequest.getConnections();
		List<String> opList = new LinkedList<String>();
		String src;
		String dest;
		int noOfIterations = 3; // 3 or more recommended
		for (int i = 0; i < noOfIterations; i++) {
			for (Connection c : conn) {
				src = c.getUi_attributes().getSourceName();
				dest = c.getUi_attributes().getDestinationName();
				int srcIndex = opList.indexOf(src);
				int destIndex = opList.indexOf(dest);
				if (srcIndex != -1) {
					if (destIndex != -1) {
						if ((destIndex - srcIndex) > 1) {
							opList.add(srcIndex + 1, opList.remove(destIndex));
						} else if ((srcIndex - destIndex) > 1) {
							opList.add(destIndex, opList.remove(srcIndex));
						}
					} else {
						opList.add(srcIndex + 1, dest);
					}
				} else if (destIndex != -1) {
					opList.add(destIndex, src);
				} else {
					int nextIndex = opList.size();
					opList.add(nextIndex, src);
					opList.add(nextIndex + 1, dest);
				}
			}
		}
		Operators op;
		String operatorName;
		if (opList.size() > 0) {
			Operators[] rearrangedOperators = new Operators[opList.size()];
			Iterator<Operators> it = bpFlowRequest.getOperators().iterator();
			while (it.hasNext()) {
				op = it.next();
				operatorName = op.getKey();
				rearrangedOperators[opList.indexOf(operatorName)] = op;
			}
			bpFlowRequest.setOperators(Arrays.asList(rearrangedOperators));
		}
	}

}
