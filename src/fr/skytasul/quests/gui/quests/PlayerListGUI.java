package fr.skytasul.quests.gui.quests;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.skytasul.quests.Quest;
import fr.skytasul.quests.QuestsConfiguration;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.gui.CustomInventory;
import fr.skytasul.quests.gui.Inventories;
import fr.skytasul.quests.gui.ItemUtils;
import fr.skytasul.quests.gui.misc.ConfirmGUI;
import fr.skytasul.quests.players.PlayerAccount;
import fr.skytasul.quests.stages.StageManager.Source;
import fr.skytasul.quests.utils.Lang;
import fr.skytasul.quests.utils.Utils;
import fr.skytasul.quests.utils.XMaterial;

public class PlayerListGUI implements CustomInventory {

	private Inventory inv;
	private Player open;
	private PlayerAccount acc;
	
	private int page = 0;
	private Category cat = Category.NONE;
	private List<Quest> quests;
	
	public PlayerListGUI(PlayerAccount acc){
		this.acc = acc;
	}
	
	public Inventory open(Player p) {
		open = p;
        
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
		inv = Bukkit.createInventory(null, QuestsConfiguration.getSlots(), Lang.INVENTORY_PLAYER_LIST.format(acc.getOfflinePlayer().getName()));
		//inv = Bukkit.createInventory(null, 45, Lang.INVENTORY_PLAYER_LIST.format(acc.getOfflinePlayer().getName()));
		
		setBarItem(0 + 1, QuestsConfiguration.getPrevPage());
		setBarItem(4 + 1, QuestsConfiguration.getNextPage());
		
		setBarItem(1 + 1, ItemUtils.item(XMaterial.WRITTEN_BOOK, "¡±r" + Lang.finisheds.toString()));
		setBarItem(2 + 1, ItemUtils.item(XMaterial.BOOK, "¡±r" + Lang.inProgress.toString()));
		setBarItem(3 + 1, ItemUtils.item(XMaterial.WRITABLE_BOOK, "¡±r" + Lang.notStarteds.toString()));
		for (int i = 0; i < 9; i++) {
			try {
				ItemStack header = QuestsConfiguration.getHeader().get(i);
				inv.setItem(i, header);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}
		for (int i = 0; i < 9 ; i++) {
			try {
				ItemStack footer = QuestsConfiguration.getFooter().get(i);
				inv.setItem(i + 54, footer);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}
		/*setBarItem(0, ItemUtils.itemLaterPage());
		setBarItem(4, ItemUtils.itemNextPage());
		
		setBarItem(1, ItemUtils.item(XMaterial.WRITTEN_BOOK, "¡±r" + Lang.finisheds.toString()));
		setBarItem(2, ItemUtils.item(XMaterial.BOOK, "¡±r" + Lang.inProgress.toString()));
		setBarItem(3, ItemUtils.item(XMaterial.WRITABLE_BOOK, "¡±r" + Lang.notStarteds.toString()));*/
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
		
		setCategory(Category.IN_PROGRESS);

		inv = p.openInventory(inv).getTopInventory();
		return inv;
	}
	
	private void setCategory(Category category){
		if (cat != Category.NONE) toggleCategoryEnchanted();
		cat = category;
		page = 0;
		toggleCategoryEnchanted();
		setItems();
		
		DyeColor color = cat == Category.FINISHED ? DyeColor.GREEN: (cat == Category.IN_PROGRESS ? DyeColor.YELLOW : DyeColor.RED);
        
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
		for (int i = 0; i < 5; i++) inv.setItem(i * 9 + 7 + 9, ItemUtils.itemSeparator(color));
		//for (int i = 0; i < 5; i++) inv.setItem(i * 9 + 7, ItemUtils.itemSeparator(color));
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
	}
	
	private void setItems(){
		for (int i = 0; i < 35; i++) setMainItem(i, null);
		switch (cat){
		
		case FINISHED:
			quests = QuestsAPI.getQuestsFinished(acc);
			for (int i = page * 35; i < quests.size(); i++){
				if (i == (page + 1) * 35) break;
				Quest qu = quests.get(i);
				String[] lore = null;
				if (qu.isRepeatable()){
					int timeLeft = qu.getTimeLeft(acc);
					if (timeLeft == 0){
						lore = new String[]{Lang.canRedo.toString()};
					}else if (timeLeft > 0) {
						lore = new String[]{Lang.timeWait.format(timeLeft)};
					}
				}
				setMainItem(i - page * 35, createQuestItem(qu, lore));
			}
			break;
		
		case IN_PROGRESS:
			quests = QuestsAPI.getQuestsStarteds(acc);
			for (int i = page * 35; i < quests.size(); i++){
				if (i == (page + 1) * 35) break;
				Quest qu = quests.get(i);
				List<String> desc = Utils.splitOnSpace(qu.getStageManager().getDescriptionLine(acc, Source.MENU), 45);
				if (QuestsConfiguration.allowPlayerCancelQuest()) {
					desc.add(null);
					desc.add(Lang.cancelLore.toString());
				}
				setMainItem(i - page * 35, createQuestItem(qu, desc.toArray(new String[0])));
			}
			break;
			
		case NOT_STARTED:
			quests = QuestsAPI.getQuestsUnstarted(acc, true);
			for (int i = page * 35; i < quests.size(); i++){
				if (i == (page + 1) * 35) break;
				Quest qu = quests.get(i);
				setMainItem(i - page * 35, createQuestItem(qu, Utils.format(Lang.TALK_NPC.toString(), qu.getStarter().getName())));
			}
			break;
		}
	}
	
	private int setMainItem(int mainSlot, ItemStack is){
		int line = (int) Math.floor(mainSlot * 1.0 / 7.0);
		int slot = mainSlot + (2 * line);
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
		inv.setItem(slot + 9, is);
		//inv.setItem(slot, is);
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
		return slot;
	}
	
	private int setBarItem(int barSlot, ItemStack is){
		int slot = barSlot * 9 + 8;
		inv.setItem(slot, is);
		return slot;
	}
	
	private ItemStack createQuestItem(Quest qu, String... lore){
		return ItemUtils.item(QuestsConfiguration.getItemMaterial(), open.hasPermission("beautyquests.seeId") ? Lang.formatId.format(qu.getName(), qu.getID()) : Lang.formatNormal.format(qu.getName()), lore);
	}
	
	private void toggleCategoryEnchanted(){
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
		//ItemStack is = inv.getItem(cat.ordinal() * 9 + 8);
		ItemStack is = inv.getItem(cat.ordinal() * 9 + 8 + 9);
		/*
		 * 
		 * MODIFIED MY i998979
		 * 
		 * */
		if (!ItemUtils.hasEnchant(is, Enchantment.DURABILITY)){
			String s = ItemUtils.getName(ItemUtils.addEnchant(is, Enchantment.DURABILITY, 0));
			ItemUtils.name(is, "¡±b¡±l" + s.substring(2));
		}else{
			String s = ItemUtils.getName(ItemUtils.removeEnchant(is, Enchantment.DURABILITY));
			ItemUtils.name(is, "¡±r" + s.substring(4));
		}
	}

	
	public boolean onClick(Player p, Inventory inv, ItemStack current, int slot, ClickType click) {
		PlayerListGUI thiz = this;
		switch (slot % 9){
		case 8:
			int barSlot = (slot - 8) / 9;
			switch (barSlot){
			/*
			 * 
			 * MODIFIED MY i998979
			 * 
			 * */
			case 0 + 1:
				if (page == 0) break;
				page--;
				setItems();
				break;
			case 4 + 1:
				page++;
				setItems();
				break;
				
			case 1 + 1:
			case 2 + 1:
			case 3 + 1:
				setCategory(Category.values()[barSlot - 1]);
				break;
				
			/*case 0:
				if (page == 0) break;
				page--;
				setItems();
				break;
			case 4:
				page++;
				setItems();
				break;
				
			case 1:
			case 2:
			case 3:
				setCategory(Category.values()[barSlot]);
				break;*/
				
				/*
				 * 
				 * MODIFIED MY i998979
				 * 
				 * */
			}
			break;
			
		case 7:
			break;
			
		default:
			if (QuestsConfiguration.allowPlayerCancelQuest() && cat == Category.IN_PROGRESS) {
				int id = (int) (slot - (Math.floor(slot * 1D / 9D)*2) + page*35);
				Inventories.create(p, new ConfirmGUI(() -> {
						quests.get(id).cancelPlayer(acc);
				}, () -> {
						p.openInventory(inv);
						Inventories.put(p, thiz, inv);
				}));
			}
			break;
			
		}
		return true;
	}
	
	public CloseBehavior onClose(Player p, Inventory inv){
		return CloseBehavior.REMOVE;
	}

	enum Category{
		NONE, FINISHED, IN_PROGRESS, NOT_STARTED;
	}
	
}
