package heig.dai.http.api.Controllers;

import heig.dai.http.api.Models.Monster;
import heig.dai.http.api.Models.*;
import io.javalin.http.Context;
import java.util.concurrent.ConcurrentHashMap;


import java.util.Map;
import java.util.HashMap;

public class MonsterController{

    private final ConcurrentHashMap<Integer, Monster> monsters = new ConcurrentHashMap<Integer, Monster>();
    private int nextId = 0;
    public MonsterController(){
        String descBarioth ="The snow-white flying wyvern with huge tusks found in the frozen tundra." +
                             " It uses its forelegs and tail to traverse ice with ease.";
        Map<ElementType,Integer> weaknessBarioth = new HashMap<>();
        weaknessBarioth.put(ElementType.FIRE, 3);
        weaknessBarioth.put(ElementType.THUNDER, 2);

        Map<ElementType,Integer> resistanceBarioth = new HashMap<>();
        resistanceBarioth.put(ElementType.WATER, 4);
        resistanceBarioth.put(ElementType.ICE, 4);
        resistanceBarioth.put(ElementType.DRAGON, 1);

        monsters.put(nextId++, new Monster("Barioth", descBarioth, MonsterType.FLYING_WYVERN, ElementType.ICE,
                weaknessBarioth, resistanceBarioth, 19200));
    }

    public void getById(Context ctx){
        int id = recoverId(ctx);
        if(id >= nextId){
            ctx.status(404);
        }else {
            ctx.json(monsters.get(recoverId(ctx)));
        }
    }

    public void getByName(Context ctx){
        String name = ctx.pathParam("name").toLowerCase();
        for (Map.Entry<Integer, Monster> monster : monsters.entrySet()) {
            if(monster.getValue().name.toLowerCase().equals(name)){
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
        int id = recoverId(ctx);
        if(id < nextId) {
            monsters.remove(recoverId(ctx));
            ctx.status(204);
        }else{
            ctx.status(404);
        }
    }

    public void update(Context ctx){
        Monster monster = ctx.bodyAsClass(Monster.class);
        int id = recoverId(ctx);
        if(id < nextId) {
            monsters.put(recoverId(ctx), monster);
            ctx.status(200);
        }else{
            ctx.status(404);
        }
    }

    public void create(Context ctx){
        Monster monster = ctx.bodyAsClass(Monster.class);
        monsters.put(nextId++, monster);
    }

    public void getWeakness(Context ctx){
        int id = recoverId(ctx);
        if(id < nextId) {
            ctx.json(monsters.get(recoverId(ctx)).weakness);
        }else{
            ctx.status(404);
        }
    }


    public void updateStats(Context ctx){
        int id = recoverId(ctx);
        if(id < nextId) {
            int size = Integer.parseInt(ctx.body().replaceAll("[\\D]", ""));
            monsters.get(id).updateStats(size);
        }else{
            ctx.status(404);
        }
    }




    private int recoverId(Context ctx){
        return Integer.parseInt(ctx.pathParam("id"));
    }

}