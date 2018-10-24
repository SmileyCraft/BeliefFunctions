package BeliefFunctions;

import java.text.DecimalFormat;
import java.util.*;

public class Main {
	private static final List<String> empty = Arrays.asList(new String[] {});
	private static final List<String> d = Arrays.asList(new String[] {"D"});
	private static final List<String> c = Arrays.asList(new String[] {"C"});
	private static final List<String> cd = Arrays.asList(new String[] {"C", "D"});
	private static final List<String> b = Arrays.asList(new String[] {"B"});
	private static final List<String> bd = Arrays.asList(new String[] {"B", "D"});
	private static final List<String> bc = Arrays.asList(new String[] {"B", "C"});
	private static final List<String> bcd = Arrays.asList(new String[] {"B", "C", "D"});
	private static final List<String> a = Arrays.asList(new String[] {"A"});
	private static final List<String> ad = Arrays.asList(new String[] {"A", "D"});
	private static final List<String> ac = Arrays.asList(new String[] {"A", "C"});
	private static final List<String> acd = Arrays.asList(new String[] {"A", "C", "D"});
	private static final List<String> ab = Arrays.asList(new String[] {"A", "B"});
	private static final List<String> abd = Arrays.asList(new String[] {"A", "B", "D"});
	private static final List<String> abc = Arrays.asList(new String[] {"A", "B", "C"});
	private static final List<String> abcd = Arrays.asList(new String[] {"A", "B", "C", "D"});
	
	private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");
	
	public static void main(String[] args) {
		basicTest();
		System.out.println();
		biggerTest();
		System.out.println();
		probabilityTest();
	}
	
	private static void basicTest(){
		Map<List<String>, Double> nonZeroBeliefAssignments1 = new HashMap<List<String>, Double>();
		nonZeroBeliefAssignments1.put(ab, 1.0);
		nonZeroBeliefAssignments1.put(a, 1.0);
		nonZeroBeliefAssignments1.put(b, 8.0);
		
		BasicBeliefAssignment<String> basicBeliefAssignment1 =
				new BasicBeliefAssignment<String>(ab, nonZeroBeliefAssignments1);
		
		Map<List<String>, Double> nonZeroBeliefAssignments2 = new HashMap<List<String>, Double>();
		nonZeroBeliefAssignments2.put(ab, 1.0);
		nonZeroBeliefAssignments2.put(a, 3.0);
		nonZeroBeliefAssignments2.put(b, 1.0);
		
		BasicBeliefAssignment<String> basicBeliefAssignment2 =
				new BasicBeliefAssignment<String>(ab, nonZeroBeliefAssignments2);
		
		printEverything(Arrays.asList(new Object[] {
				basicBeliefAssignment1, basicBeliefAssignment2,
				BasicBeliefAssignment.combine(basicBeliefAssignment1, basicBeliefAssignment2)}));
	}
	
	private static void biggerTest(){
		Map<List<String>, Double> nonZeroBeliefAssignments1 = new HashMap<List<String>, Double>();
		nonZeroBeliefAssignments1.put(bcd, 3.0);
		nonZeroBeliefAssignments1.put(abcd, 1.0);
		
		BasicBeliefAssignment<String> basicBeliefAssignment1 =
				new BasicBeliefAssignment<String>(abcd, nonZeroBeliefAssignments1);
		
		Map<List<String>, Double> nonZeroBeliefAssignments2 = new HashMap<List<String>, Double>();
		nonZeroBeliefAssignments2.put(b, 2.0);
		nonZeroBeliefAssignments2.put(d, 2.0);
		nonZeroBeliefAssignments2.put(abcd, 1.0);
		
		BasicBeliefAssignment<String> basicBeliefAssignment2 =
				new BasicBeliefAssignment<String>(abcd, nonZeroBeliefAssignments2);
		
		printEverything(Arrays.asList(new Object[] {
				basicBeliefAssignment1, basicBeliefAssignment2,
				BasicBeliefAssignment.combine(basicBeliefAssignment1, basicBeliefAssignment2)}));
	}
	
	private static void probabilityTest(){
		Map<List<String>, Double> nonZeroBeliefAssignments1 = new HashMap<List<String>, Double>();
		nonZeroBeliefAssignments1.put(a, 9.0);
		nonZeroBeliefAssignments1.put(bc, 1.0);
		
		BasicBeliefAssignment<String> basicBeliefAssignment1 =
				new BasicBeliefAssignment<String>(abc, nonZeroBeliefAssignments1);
		
		Map<List<String>, Double> nonZeroBeliefAssignments2 = new HashMap<List<String>, Double>();
		nonZeroBeliefAssignments2.put(c, 4.0);
		nonZeroBeliefAssignments2.put(ab, 1.0);
		
		BasicBeliefAssignment<String> basicBeliefAssignment2 =
				new BasicBeliefAssignment<String>(abc, nonZeroBeliefAssignments2);
		
		printEverything(Arrays.asList(new Object[] {
				basicBeliefAssignment1, basicBeliefAssignment2,
				BasicBeliefAssignment.combine(basicBeliefAssignment1, basicBeliefAssignment2)}));
	}
	
	@SuppressWarnings("unchecked")
	private static void printEverything(List<Object> basicBeliefAssignments){
		printBeliefAssignments((BasicBeliefAssignment<String>) basicBeliefAssignments.get(0), "m1");
		for (int i = 1; i < basicBeliefAssignments.size(); i++){
			System.out.println();
			printBeliefAssignments((BasicBeliefAssignment<String>) basicBeliefAssignments.get(i), "m" + (i + 1));
		}
		for (int i = 0; i < basicBeliefAssignments.size(); i++){
			System.out.println();
			printBeliefs((BasicBeliefAssignment<String>) basicBeliefAssignments.get(i), "Bel" + (i + 1));
		}
		for (int i = 0; i < basicBeliefAssignments.size(); i++){
			System.out.println();
			printCumulativeBeliefAssignments(
					(BasicBeliefAssignment<String>) basicBeliefAssignments.get(i), "cbm" + (i + 1));
		}
	}
	
	private static void printBeliefAssignments(BasicBeliefAssignment<String> basicBeliefAssignment, String name){
		Set<String> eventSpace = basicBeliefAssignment.getEventSpace();
		for (List<String> subspace : powerList(eventSpace)){
			Collections.reverse(subspace);
			System.out.print(name + "(");
			System.out.print(toSetNotation(subspace, eventSpace.size()));
			System.out.println(") = " + decimalFormat.format(basicBeliefAssignment.getBeliefAssignment(subspace)));
		}
	}
	
	private static void printBeliefs(BasicBeliefAssignment<String> basicBeliefAssignment, String name){
		Set<String> eventSpace = basicBeliefAssignment.getEventSpace();
		for (List<String> subspace : powerList(eventSpace)){
			Collections.reverse(subspace);
			System.out.print(name + "(");
			System.out.print(toSetNotation(subspace, eventSpace.size()));
			System.out.println(") = " + decimalFormat.format(basicBeliefAssignment.getBelief(subspace)));
		}
	}
	
	private static void printCumulativeBeliefAssignments(
			BasicBeliefAssignment<String> basicBeliefAssignment, String name){
		Set<String> eventSpace = basicBeliefAssignment.getEventSpace();
		List<Pair<Pair<List<String>, List<String>>, StringBuilder>> doublePowerList = doublePowerList(eventSpace);
		for (Pair<Pair<List<String>, List<String>>, StringBuilder> doubleSubspace : doublePowerList){
			doubleSubspace.second.reverse();
			System.out.print(name + "(");
			System.out.print(doubleSubspace.second);
			System.out.println(") = " + decimalFormat.format(basicBeliefAssignment.getCumulativeBeliefAssignment(
					doubleSubspace.first.first, doubleSubspace.first.second)));
		}
	}
	
	private static String toSetNotation(List<String> list, int omegaSize){
		if (list.isEmpty()){
			return "∅";
		}
		else if (list.size() == omegaSize){
			return "Ω";
		}
		else{
			StringBuilder sb = new StringBuilder("{" + list.get(0));
			for (int i = 1; i < list.size(); i++){
				sb.append(", " + list.get(i));
			}
			sb.append("}");
			return sb.toString();
		}
	}
	
	private static List<List<String>> powerList(Collection<String> collection){
		List<String> list = new ArrayList<String>(collection);
		Collections.sort(list);
		Collections.reverse(list);
		if (list.isEmpty()){
			List<List<String>> powerList = new ArrayList<List<String>>();
			powerList.add(new ArrayList<String>());
			return powerList;
		}
		String last = list.get(list.size() - 1);
		list.remove(list.size() - 1);
		List<List<String>> powerList1 = powerList(list);
		List<List<String>> powerList2 = powerList(list);
		for (List<String> subList : powerList2){
			subList.add(last);
			powerList1.add(subList);
		}
		return powerList1;
	}
	
	private static List<Pair<Pair<List<String>, List<String>>, StringBuilder>> doublePowerList(
			Collection<String> collection){
		List<String> list = new ArrayList<String>(collection);
		Collections.sort(list);
		Collections.reverse(list);
		if (list.isEmpty()){
			List<Pair<Pair<List<String>, List<String>>, StringBuilder>> doublePowerList =
					new ArrayList<Pair<Pair<List<String>, List<String>>, StringBuilder>>();
			doublePowerList.add(new Pair<Pair<List<String>, List<String>>, StringBuilder>(
					new Pair<List<String>, List<String>>(
					new ArrayList<String>(), new ArrayList<String>()), new StringBuilder()));
			return doublePowerList;
		}
		String last = list.get(list.size() - 1);
		list.remove(list.size() - 1);
		List<Pair<Pair<List<String>, List<String>>, StringBuilder>> doublePowerList1 = doublePowerList(list);
		List<Pair<Pair<List<String>, List<String>>, StringBuilder>> doublePowerList2 = doublePowerList(list);
		List<Pair<Pair<List<String>, List<String>>, StringBuilder>> doublePowerList3 = doublePowerList(list);
		for (Pair<Pair<List<String>, List<String>>, StringBuilder> doubleSubspace : doublePowerList1){
			doubleSubspace.second.append("*");
		}
		for (Pair<Pair<List<String>, List<String>>, StringBuilder> doubleSubspace : doublePowerList2){
			doubleSubspace.first.second.add(last);
			doubleSubspace.second.append("0");
			doublePowerList1.add(doubleSubspace);
		}
		for (Pair<Pair<List<String>, List<String>>, StringBuilder> doubleSubspace : doublePowerList3){
			doubleSubspace.first.first.add(last);
			doubleSubspace.second.append("1");
			doublePowerList1.add(doubleSubspace);
		}
		return doublePowerList1;
	}
	
	private static class Pair<A, B> {
		public A first;
		public B second;
		
		public Pair(A a, B b){
			first = a;
			second = b;
		}
	}
}
