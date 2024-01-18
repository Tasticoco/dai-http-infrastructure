package heig.dai.http.api;
import heig.dai.http.api.Controllers.MonsterController;
import io.javalin.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        MonsterController monsterController = new MonsterController();

        app.get("/api/monsters/{id}", monsterController::getById);
        app.get("/api/monsters", monsterController::getAll);
        app.post("/api/monsters", monsterController::create);
        app.put("/api/monsters/{id}", monsterController::update);
        app.delete("/api/monsters/{id}", monsterController::delete);
        app.get("/api/monsters/{id}/weakness", monsterController::getWeakness);
        app.put("/api/monsters/{id}/hunted", monsterController::updateStats);
        app.get("/api/monsters/name/{name}", monsterController::getByName);
    }
}