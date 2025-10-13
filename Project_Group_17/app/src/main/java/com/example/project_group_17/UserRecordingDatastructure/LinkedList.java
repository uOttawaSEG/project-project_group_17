package com.example.project_group_17.UserRecordingDatastructure;

public class LinkedList<E> {
    private Node<E> head;
    public LinkedList(){
        head = new Node<E>(null);
    }
    public void newUser(E user){
        Node<E> newNode = new Node<>(user);
        if(head.getNext()==null){
            head.setNext(newNode);
        } else{
            Node<E> traverse = head.getNext();
            while(traverse.getNext()!=null){
                traverse=traverse.getNext();
            }
            traverse.setNext(newNode);
        }
    }

    public Node<E> userExists(String email, String password){
        //Will loop through the whole list of users and compare the emails and passwords
        //If it finds a match it will return true and that user will be logged on if false
        //the login screen will display a toast saying that user does not exist
        //check email and password or register new user
        if(head.getNext()==null){
            return null;
        }
        Node<E> traverse = head.getNext();
        while(head.getNext()!=null){
            if(traverse.getEmail().equals(email)&&traverse.getPassword().equals(password)){
                return traverse;
            }
        }
        return null;
    }
}
