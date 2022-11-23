package sistemasdistribuidos;

import sistemasdistribuidos.factories.SynchronousFactory;
import sistemasdistribuidos.interfaces.ISynchronousService;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        ISynchronousService synchronousService = SynchronousFactory.getSynchronousService();
        synchronousService.Execute(args);
    }
}
