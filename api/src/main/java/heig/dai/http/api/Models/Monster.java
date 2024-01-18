package heig.dai.http.api.Models;


import java.util.Map;

public class Monster {

    public String name;
    public String description;
    public MonsterType species;
    public ElementType element;
    public Map<ElementType,Integer> weakness;
    public Map<ElementType,Integer> resistance;
    public int maxHP;
    public int investigationXP;
    public int biggestEncounter;
    public int smallestEncounter;
    public int nbHunted;

    public Monster(){}

    public Monster(String name, String description, MonsterType species, ElementType element,
                   Map<ElementType, Integer> weakness, Map<ElementType, Integer> Resistance, int maxHP){
        this(name, description, species, element, weakness, Resistance, maxHP, 0, 0, 0, 0);
    }

    public Monster(String name, String description, MonsterType species, ElementType element,
            Map<ElementType,Integer> weakness, Map<ElementType,Integer> resistance, int maxHP, int investigationXP,
            int biggestEncounter, int smallestEncounter, int nbHunted){
        this.name = name;
        this.description = description;
        this.species = species;
        this.element = element;
        this.weakness = weakness;
        this.resistance = resistance;
        this.maxHP = maxHP;
        this.investigationXP = investigationXP;
        this.biggestEncounter = biggestEncounter;
        this.smallestEncounter = smallestEncounter;
        this.nbHunted = nbHunted;
    }

    /**
     * Update the stats of the monster when hunted
     * @param size size of the monster hunted
     */
    public void updateStats(int size) {
        if (size > biggestEncounter) {
            biggestEncounter = size;
        }
        if (size < smallestEncounter) {
            smallestEncounter = size;
        }
        ++nbHunted;
    }
}