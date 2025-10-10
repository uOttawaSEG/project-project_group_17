package com.example.project_group_17.UserRecordingDatastructure;

import com.example.project_group_17.UserHierarchy.User;

public class Node<E> {
    private Node<E> next;
    private Node<E> prev;
    private E user;

    public Node(E user){
        this.user = user;
    }

    public String getPassword() {
        return ((User)user).getPassword();
    }

    public String getEmail() {
        return ((User)user).getEmail();
    }

    public String getUserType() {
        return ((User)user).getUserType();
    }

    public Node<E> getNext() {
        return next;
    }
    public void setNext(Node<E> next){
        this.next = next;
    }
    public Node<E> getPrev() {
        return prev;
    }
    public void setPrev(Node<E> prev){
        this.prev = prev;
    }
}
