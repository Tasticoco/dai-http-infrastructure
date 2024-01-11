package heig.dai.http.api;
import heig.dai.http.api.Controllers.MonsterController;
import io.javalin.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        MonsterController monsterController = new MonsterController();

        app.get("/monsters/{id}", monsterController::getById);
        app.get("/monsters/", monsterController::getAll);
        app.post("/monsters/", monsterController::create);
        app.put("/monsters/{id}", monsterController::update);
        app.delete("/monsters/{id}", monsterController::delete);
        app.get("/monsters/{id}/weak", monsterController::getWeakness);
        app.put("/monsters/{id}/hunted", monsterController::updateStats);
    }
}