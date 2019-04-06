package com.qq44920040.Minecraft.AutoCompose;

import com.qq44920040.Minecraft.AutoCompose.Entity.Compose;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


public class ZythonMain extends JavaPlugin {
    private static String MsgCantCompose;
    private static String MsgPermission;
    private static HashMap<String, List<Compose>> ComposeKey = new HashMap<>();
    private static String MsgPackgeFull;
    private static HashMap<String,String[]> IsCommands = new HashMap<>();
    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"config.yml");
        if (!file.exists()){
            saveDefaultConfig();
        }
        ReloadConfig();
        super.onEnable();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player&&label.equalsIgnoreCase("AutoCompose")&&args.length==1){
//            System.out.println("ok");
            if (args[0].equalsIgnoreCase("reload")){
                ReloadConfig();
            }
            if (ComposeKey.containsKey(args[0])){
//                System.out.println("确认有合成列表");
                if (!sender.hasPermission("AutoCompose."+args[0])){
                    sender.sendMessage(MsgPermission);
                    return false;
                }
                List<Compose> Itemlist = ComposeKey.get(args[0]);
                Player player =(Player)sender;
                Inventory inv = player.getInventory();
                List<ItemStack> itemStacks =Arrays.stream(inv.getContents()).filter(itemStack -> itemStack!=null&&itemStack.getType()!=Material.AIR).collect(Collectors.toList());
//                System.out.println(itemStacks.size()+"背包有");
                String HasNumber = IsCommands.get(args[0])[1];
//                System.out.println("比对背包"+HasNumber);
                if (!(itemStacks.size()<=Integer.parseInt(HasNumber))){
                    player.sendMessage(MsgPackgeFull.replace("{Number}",HasNumber));
                    return false;
                }
//                if (IsCommands.containsKey(args[0])){
//
//                }else {
//                    player.sendMessage(MsgPackgeFull);
//                    return false;
//                }
 //               List<ItemStack> HaveItems = new ArrayList<>();
                HashMap<ItemStack,Integer> LastItems = new HashMap<>();
                for (Compose compose :Itemlist) {
                    System.out.println(compose.toString());
                    for (ItemStack item : itemStacks) {
                        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
                            ItemMeta itemMeta = item.getItemMeta();
                            if (itemMeta.hasLore()) {
                                String Lores = itemMeta.getLore().toString();
                                String DisPlayer = itemMeta.getDisplayName();
                                System.out.println(DisPlayer);
                                System.out.println(compose.DisPlayerKey);
                                if (DisPlayer!=null&&DisPlayer.contains(compose.DisPlayerKey)){
                                    String[] arrys = compose.LoreKey.split("//|");
//                                    System.out.println("进入列");
                                    if (HasComposeLore(Lores,arrys)){
                                        LastItems.put(item,compose.Stack);
                                    }
                                }
                            }
                        }
                    }
                }
//                System.out.println(Itemlist.size()+"he "+LastItems.size());
                if (LastItems.size()==Itemlist.size()){
//                    System.out.println("正在合成");
                    Set<ItemStack> removeitemStack = LastItems.keySet();
                    for (ItemStack item:removeitemStack){
                        inv.remove(item);
                        System.out.println(item.getAmount());
                        item.setAmount(item.getAmount()-LastItems.get(item));
                        inv.addItem(item);
                    }
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),IsCommands.get(args[0])[0].replace("{Player}",player.getName()));
                }else {
                    player.sendMessage(MsgCantCompose);
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    private boolean HasComposeLore(String Lores,String[] arrys){
        int a=1;
        for (String keys:arrys){
            if (Lores.contains(keys)){
                a++;
            }
        }
//        System.out.println(arrys.length+"判断长度");
//        System.out.println(a);
        return arrys.length==a;
    }

    private void ReloadConfig() {
        reloadConfig();
        Set<String> mines = getConfig().getConfigurationSection("AutoCompose").getKeys(false);
        for (String temp : mines) {
            Set<String> items = getConfig().getConfigurationSection("AutoCompose."+temp).getKeys(false);
            List<Compose> Itemlist = new ArrayList<>();
            for (String itemname:items){
                String DisPlayerKey = getConfig().getString("AutoCompose."+temp+"."+itemname+".DisPlayerKey");
                String LoreKey = getConfig().getString("AutoCompose."+temp+"."+itemname+".LoreKey");
                int Stack = getConfig().getInt("AutoCompose."+temp+"."+itemname+".Stack");
                Compose compose = new Compose(DisPlayerKey, LoreKey, Stack);
                System.out.println(compose);
                Itemlist.add(compose);
            }
            ComposeKey.put(temp, Itemlist);
        }
        Set<String> Commands = getConfig().getConfigurationSection("IsCommands").getKeys(false);
        for (String cm:Commands){
            IsCommands.put(cm,new String[]{getConfig().getString("IsCommands."+cm+".cmd"),getConfig().getString("IsCommands."+cm+".PackgeSize")});
        }
        MsgPackgeFull = getConfig().getString("MsgPackgeFull");
        MsgCantCompose =getConfig().getString("MsgCantCompose");
        MsgPermission = getConfig().getString("MsgPermission");
    }
}
