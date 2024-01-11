package heig.dai.http.api.Controllers;

import heig.dai.http.api.Models.Monster;
import heig.dai.http.api.Models.*;
import io.javalin.http.Context;
import java.util.concurrent.ConcurrentHashMap;


import java.util.Map;
import java.util.HashMap;

public class MonsterController{

    private final ConcurrentHashMap<Integer, Monster> monsters = new ConcurrentHashMap<>();
    int nextId = 0;
    public MonsterController(){
        String descBarrioth ="The snow-white flying wyvern with huge tusks found in the frozen tundra." +
                             " It uses its forelegs and tail to traverse ice with ease.";
        Map<ElementType,Integer> weaknessBarrioth = new HashMap<>();
        weaknessBarrioth.put(ElementType.FIRE, 3);
        weaknessBarrioth.put(ElementType.THUNDER, 2);

        Map<ElementType,Integer> resistanceBarrioth = new HashMap<>();
        resistanceBarrioth.put(ElementType.WATER, 4);
        resistanceBarrioth.put(ElementType.ICE, 4);
        resistanceBarrioth.put(ElementType.DRAGON, 1);

        monsters.put(nextId++,(new Monster("Barioth", descBarrioth, MonsterType.FLYING_WYVERN, ElementType.ICE,
                weaknessBarrioth, resistanceBarrioth, 19200)));
    }

    public void getById(Context ctx){
        ctx.json(monsters.get(recoverId(ctx)));
    }

    public void getByName(Context ctx){
        String name = ctx.pathParam("name");
        for (Map.Entry<Integer, Monster> monster : monsters.entrySet()) {
            if(monster.getValue().name.equals(name)){
                ctx.json(monster);
                return;
            }
        }
        ctx.status(404);
    }

    public void getAll(Context ctx){
        ctx.json(monsters);
    }

    public void delete(Context ctx){
        monsters.remove(recoverId(ctx));
        ctx.status(204);
    }

    public void update(Context ctx){
        Monster monster = ctx.bodyAsClass(Monster.class);
        monsters.put(recoverId(ctx), monster);
        ctx.status(200);
    }

    public void create(Context ctx){
        Monster monster = ctx.bodyAsClass(Monster.class);
        monsters.put(nextId++, monster);
    }

    public void getWeakness(Context ctx){
        Monster monster = monsters.get(recoverId(ctx));
        ctx.json(monster.weakness);
    }

    public void updateStats(Context ctx){
        int size = Integer.parseInt(ctx.pathParam("size"));
        Monster monster = monsters.get(recoverId(ctx));
        monster.updateStats(size);
    }




    private int recoverId(Context ctx){
        return Integer.parseInt(ctx.pathParam("id"));
    }

}