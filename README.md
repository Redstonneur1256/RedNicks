# RedNicks
Simple plugin to change name on spigot


Chage player nick manually:
```java
import fr.redstonneur1256.rednicks.RedNicks;
import fr.redstonneur1256.rednicks.utils.NickData;
import org.bukkit.entity.Player;

void someMethod() {
    Player player = ...;
    RedNicks redNicks = RedNicks.get();
    NickData data = redNicks.getData(player);
    try {
        data.setNick("New nick name");
    } catch (Exception exception) { // Nick change failed
        exception.printStackTrace();
    }
}
```

Use your own permission system without having to add player permission:
```java
RedNicks redNicks = RedNicks.get();
redNicks.setPermissionHolder(new PermissionHolder() {
    @Override
    public boolean hasPermission(Player player, Permission permission) {
        return MyOwnSystem.hasNickPermission(player);
    }
});
```
