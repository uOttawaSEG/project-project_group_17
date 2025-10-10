package com.example.project_group_17.UserRecordingDatastructure;

public class LinkedList<User> {
    private Node<User> head;
    private Node<User> tail;
    public LinkedList(){
        head = new Node<User>(null);
    }
    public void newUser(User user){
        Node<User> newNode = new Node(user);
        if(head.getNext()==null){
            head.setNext(newNode);
        } else{
            Node traverse = head.getNext();
            while(traverse.getNext()!=null){
                traverse=traverse.getNext();
            }
            traverse.setNext(newNode);
        }
    }

    public boolean userExists(String email, String password){
        //Will loop through the whole list of users and compare the emails and passwords
        //If it finds a match it will return true and that user will be logged on if false
        //the login screen will display a toast saying that user does not exist
        //check email and password or register new user
        return false;
    }
}
