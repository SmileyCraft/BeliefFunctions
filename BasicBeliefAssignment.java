package BeliefFunctions;

import java.util.*;

/**
 * Immutable data structure for basic belief assignments and their belief functions.
 * On initialization, the basic belief assignment must be expressed.
 * The belief function will then be pre-calculated together with other data to make queries as fast as possible.
 * Contains a static method to combine two basic belief assignment objects.
 * 
 * @author Mike de Vries
 *
 * @param <E> The type of the elements of the event space.
 */
public class BasicBeliefAssignment<E> {
	private static final int[] powersOfThree = new int[] {1, 3, 9, 27, 81, 243, 729, 2187, 6561, 19683, 59049,
			177147, 531441, 1594323, 4782969, 14348907, 43046721, 129140163, 387420489, 1162261467};
	
	private List<E> events;
	private Map<E, Integer> eventIds;
	private double[] beliefAssignments;
	private double[] cumulativeBeliefAssignments;
	
	/**
	 * Create a basic belief assignment on a certain event space.
	 * Running time: O(3^|eventSpace|)
	 * 
	 * @param eventSpace
	 * 		The set of all possible outcomes of the associated experiment.
	 * 		If this set has more than 18 elements, performance issues are to be expected.
	 * @param nonZeroBeliefAssignments
	 * 		A map that associates the belief assignment to every subspace of the event space that has a non-zero
	 * 				belief assignment.
	 * 		If a subspace contains elements disjoint from the event space, these elements will be ignored.
	 * 		Entries where the resulting subspace is empty will be ignored.
	 * 		Entries with a non-positive belief assignment will be ignored.
	 * 		If the resulting subspace is empty or null, the trivial basic belief assignment will be created.
	 * 		Belief assignments of duplicate subspaces will be added up.
	 * 		If the remaining entry values do not add up to one, the assignment will be normalized such that they do.
	 */
	public BasicBeliefAssignment(Collection<? extends E> eventSpace,
			Map<? extends Collection<? extends E>, Double> nonZeroBeliefAssignments){
		initializeEvents(eventSpace);
		initializeEventIds();
		initializeBeliefAssignments(nonZeroBeliefAssignments);
		initializeCumulativeBeliefAssignments();
	}
	
	private BasicBeliefAssignment(List<E> events, double[] beliefAssignments){
		this.events = events;
		initializeEventIds();
		this.beliefAssignments = beliefAssignments;
		initializeCumulativeBeliefAssignments();
	}
	
	/**
	 * Combine two basic belief assignments according to Mike's basic belief assignment merge formula.
	 * If the basic belief assignments do not have exactly the same event space, a null value will be returned.
	 * If the basic belief assignments are contradictory, a null value will be returned.
	 * Running time: O(3^|eventSpace|)
	 * 
	 * @param basicBeliefAssignment1
	 * 		The first basic belief assignment to be merged.
	 * @param basicBeliefAssignment2
	 * 		The second basic belief assignment to be merged.
	 * @return
	 * 		The merged basic belief assignment.
	 */
	public static <E> BasicBeliefAssignment<E> combine(BasicBeliefAssignment<E> basicBeliefAssignment1,
			BasicBeliefAssignment<E> basicBeliefAssignment2){
		List<E> events = basicBeliefAssignment1.events;
		if (!events.equals(basicBeliefAssignment2.events)){
			return null;
		}
		double[] beliefAssignments = new double[1 << events.size()];
		double sum = 0.0;
		for (Query query = new Query(); query.getId() < powersOfThree[events.size()]; query.bump()){
			double add = basicBeliefAssignment1.cumulativeBeliefAssignments[query.getId()]
					* basicBeliefAssignment2.beliefAssignments[query.getFreedomComplement(events.size())];
			beliefAssignments[query.getIntersection()] += add;
			if (query.getIntersection() != 0){
				sum += add;
			}
		}
		if (sum == 0.0){
			return null;
		}
		for (int i = 1; i < (1 << events.size()); i++){
			beliefAssignments[i] /= sum;
		}
		beliefAssignments[0] = 0.0;
		return new BasicBeliefAssignment<E>(basicBeliefAssignment1.events, beliefAssignments);
	}
	
	/**
	 * Get the event space of the basic belief assignment.
	 * 
	 * @return
	 * 		The event space.
	 */
	public Set<E> getEventSpace(){
		return new HashSet<E>(events);
	}
	
	/**
	 * Get the belief assignment of a subspace of the event space.
	 * Running time: O(|subSpace|)
	 * 
	 * @param subSpace
	 * 		A subspace of the event space.
	 * 		If the set contains elements disjoint from the event space, these elements will be ignored.
	 * @return
	 * 		The belief assignment of the given subspace.
	 */
	public double getBeliefAssignment(Collection<? extends E> subspace){
		return beliefAssignments[getSubspaceId(subspace)];
	}
	
	/**
	 * Get the cumulative belief assignment over all subspaces of the event space that contain yesSubspace and are
	 * 			disjoint from noSubspace. Note that the return value will be 0 if yesSubspace and noSubspace overlap.
	 * Running time: O(|yesSubspace|+|noSubspace|)
	 * 
	 * @param yesSubspace
	 * 		The set of all events that must be in every considered subspace.
	 * @param noSubspace
	 * 		The set of all events that must not be in any considered subspace.
	 * @return
	 * 		The cumulative belief assignment over all considered subspaces.
	 */
	public double getCumulativeBeliefAssignment(
			Collection<? extends E> yesSubspace, Collection<? extends E> noSubspace){
		int queryId = powersOfThree[events.size()] - 1;
		for (E event : yesSubspace){
			queryId -= powersOfThree[eventIds.get(event)];
			if (noSubspace.contains(event)){
				return 0;
			}
		}
		for (E event : noSubspace){
			queryId -= 2 * powersOfThree[eventIds.get(event)];
		}
		return cumulativeBeliefAssignments[queryId];
	}
	
	/**
	 * Get the belief of a subspace of the event space.
	 * Running time: O(|subspace|)
	 * 
	 * @param subSpace
	 * 		A subspace of the event space.
	 * 		If the set contains elements disjoint from the event space, these elements will be ignored.
	 * @return
	 * 		The belief of the given subspace.
	 */
	public double getBelief(Collection<? extends E> subspace){
		int queryId = 0;
		for (E event : subspace){
			queryId += 2 * powersOfThree[eventIds.get(event)];
		}
		return cumulativeBeliefAssignments[queryId];
	}
	
	private void initializeEvents(Collection<? extends E> eventSpace){
		events = new ArrayList<E>(eventSpace);
	}
	
	private void initializeEventIds(){
		eventIds = new HashMap<E, Integer>();
		for (int i = 0; i < events.size(); i++){
			eventIds.put(events.get(i), i);
		}
	}
	
	private void initializeBeliefAssignments(Map<? extends Collection<? extends E>, Double> nonZeroBeliefAssignments){
		beliefAssignments = new double[1 << events.size()];
		if (nonZeroBeliefAssignments == null){
			beliefAssignments[(1 << events.size()) - 1] = 1.0;
			return;
		}
		double sum = 0.0;
		for (Collection<? extends E> subspace : nonZeroBeliefAssignments.keySet()){
			double beliefAssignment = nonZeroBeliefAssignments.get(subspace);
			int subspaceId = getSubspaceId(subspace);
			if (beliefAssignment > 0.0 && subspaceId != 0){
				beliefAssignments[subspaceId] += beliefAssignment;
				sum += beliefAssignment;
			}
		}
		if (sum == 0.0){
			beliefAssignments[(1 << events.size()) - 1] = 1.0;
		}
		else{
			for (int i = 0; i < (1 << events.size()); i++){
				beliefAssignments[i] /= sum;
			}
		}
	}
	
	private void initializeCumulativeBeliefAssignments(){
		cumulativeBeliefAssignments = new double[powersOfThree[events.size()]];
		for (Query query = new Query(); query.getId() < powersOfThree[events.size()]; query.bump()){
			Integer firstFreedom = query.getFirstFreedom();
			if (firstFreedom == null){
				cumulativeBeliefAssignments[query.getId()] = beliefAssignments[query.getIntersection()];
			}
			else{
				int pow = powersOfThree[firstFreedom];
				cumulativeBeliefAssignments[query.getId()] = cumulativeBeliefAssignments[query.getId() - pow]
						+ cumulativeBeliefAssignments[query.getId() - 2 * pow];
			}
		}
	}
	
	private int getSubspaceId(Collection<? extends E> subspace){
		int subspaceId = 0;
		for (E event : subspace){
			if (eventIds.containsKey(event)){
				subspaceId |= 1 << eventIds.get(event);
			}
		}
		return subspaceId;
	}
	
	private static class Query {
		private int id;
		private int intersection;
		private int freedomId;
		private List<Integer> freedom;
		
		public Query(){
			id = 0;
			intersection = 0;
			freedomId = 0;
			freedom = new ArrayList<Integer>();
		}
		
		public void bump(){
			id++;
			int i;
			for (i = 0; !freedom.isEmpty() && i == freedom.get(freedom.size() - 1); i++){
				freedom.remove(freedom.size() - 1);
				freedomId ^= 1 << i;
			}
			if ((id / powersOfThree[i]) % 3 == 2){
				freedom.add(i);
				freedomId |= 1 << i;
			}
			intersection ^= 1 << i;
		}
		
		public int getId(){
			return id;
		}
		
		public int getIntersection(){
			return intersection;
		}
		
		public int getFreedomComplement(int eventSpaceCardinality){
			return ((1 << eventSpaceCardinality) - 1) ^ freedomId;
		}
		
		public Integer getFirstFreedom(){
			if (freedom.isEmpty()){
				return null;
			}
			else{
				return freedom.get(0);
			}
		}
	}
}
