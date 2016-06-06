package com.mti.meetme.controller;

/**
 * Created by thiba_000 on 20/05/2016.
 */
public class TodayDesire {

    public enum Desire
    {
        Everything ("Partant pour tout"),
        Drink ("Prendre un verre"),
        Date ("Rencontre"),
        Sport ("Sport"),
        party ("Soirée"),
        play ("Jouer");

        private String desire = "";
        Desire(String name){
            this.desire = name;
        }

        public String toString(){
            return desire;
        }
    }
}
