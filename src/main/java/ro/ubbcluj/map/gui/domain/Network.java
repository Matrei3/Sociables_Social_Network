package ro.ubbcluj.map.gui.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {
    private final List<List<Integer>> friendshipList;
    private final Map<Long,Integer> numberAssociationMap = new HashMap<>();
    private final Map<Integer,Long> numberAssociationMapReverse = new HashMap<>();
    private final Integer size;

    public Network(Iterable<User> userList) {

        int numberAssociated=0;
        for(User user : userList){
            numberAssociationMap.put(user.id,numberAssociated);
            numberAssociationMapReverse.put(numberAssociated,user.id);
            numberAssociated++;
        }

        this.size = numberAssociated;
        this.friendshipList = new ArrayList<>();
        for(int i=0;i<size;i++)
            friendshipList.add(new ArrayList<>(size));
    }
    public void addFriendships(Iterable<Friendship<Long>> list){
        list.forEach(x -> {

            int node1 = numberAssociationMap.get(x.getID1());
            int node2 = numberAssociationMap.get(x.getID2());

            friendshipList.get(node1).add(node2);
            friendshipList.get(node2).add(node1);
        });
    }

    private void DFS(int v, boolean[] visited) {
        visited[v] = true;
        for (int neighbor : friendshipList.get(v)) {
            if (!visited[neighbor]) {
                DFS(neighbor, visited);
            }
        }
    }
    private int numberOfCommunities() {
        boolean[] visited = new boolean[size];
        int count = 0;
        for (int v = 0; v < size; v++) {
            if (!visited[v]) {
                DFS(v, visited);
                count++;
            }
        }
        return count;
    }
    public void printNumberOfCommunities(){
        System.out.println("The number of communities is " + numberOfCommunities());
    }
    private List<Long> biggestCommunity() {
        boolean[] visited = new boolean[size];
        boolean[] viz=new boolean[size];
        List<Long>biggestCommunityMembers=new ArrayList<>();
        int maxNumberOfMembers=0;
        for (int i = 0; i < size; i++)
            if (!visited[i]) {
                DFS(i, visited);
                int tempMemberNumber=0;
                List<Long>tempList=new ArrayList<>();
                for(int p=0;p<size;p++){
                    if(visited[p]!=viz[p]){
                        tempMemberNumber++;
                        tempList.add(numberAssociationMapReverse.get(p));
                        viz[p]=visited[p];
                    }
                    if(tempMemberNumber>maxNumberOfMembers){
                        biggestCommunityMembers.clear();
                        biggestCommunityMembers.addAll(tempList);
                        maxNumberOfMembers=tempMemberNumber;
                    }
                }
            }
        return biggestCommunityMembers;
    }
    public void printBiggestCommunity(){
        System.out.print("Biggest community member's id: ");
        biggestCommunity().stream().sorted().forEach(x -> {
            System.out.printf(x + " ");
        });
        System.out.println();
    }
}
