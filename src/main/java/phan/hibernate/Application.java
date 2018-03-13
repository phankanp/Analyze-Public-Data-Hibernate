package phan.hibernate;

import phan.hibernate.model.Prompter;

public class Application {
    public static void main(String[] args) {
        Prompter prompter = new Prompter();

        prompter.runPrompt();
    }
}
