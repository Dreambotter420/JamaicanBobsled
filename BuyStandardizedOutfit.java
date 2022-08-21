package script.behaviour.misc;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.trade.TradeUser;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;

import script.framework.Leaf;
import script.utilities.API;
import script.utilities.Sleep;
import script.utilities.Walkz;

public class BuyStandardizedOutfit extends Leaf {

    @Override
    public boolean isValid() {
    	if(API.hasCorrectGender && 
    			API.hasCorrectFacialHair && 
    			API.hasCorrectHair && 
    			API.hasCorrectPants && 
    			API.hasCorrectShirt) 
    	{
    		API.exitstop();
    		doneWithClothes = true;
    		//API.mode = API.modes.SWITCH_ESS;
    		System.exit(0);
    	}
    	return API.mode == API.modes.RANDOMIZE_OUTFIT &&
    			!doneWithClothes;
    }
   
    
    @Override
    public int onLoop() {
    	if(Dialogues.canContinue())
    	{
    		Dialogues.continueDialogue();
    		return Sleep.calculate(111, 111);
    	}
    	//step 0: get money
    	if(!gotMoney)
    	{
    		if(Inventory.contains("Coins") && Inventory.get("Coins").getAmount() >= 10000)
        	{
        		gotMoney = true;
        		return Sleep.calculate(111, 111);
        	}
    		else
    		{
    			if(Trade.isOpen(1))
    			{
    				if(!Trade.hasAcceptedTrade(TradeUser.US))
    				{
    					Trade.acceptTrade(1);
    					Sleep.sleep(666, 111);
    				}
    			}
    			else if(Trade.isOpen(2))
    			{
    				if(!Trade.hasAcceptedTrade(TradeUser.US))
    				{
    					Trade.acceptTrade(2);
    					Sleep.sleep(666, 111);
    				}
    			}
    			else if(Players.closest(API.callerName) != null)
    			{
    				Player p = Players.closest(API.callerName);
    				if(p.canReach())
    				{
    					if(p.interact("Trade with"))
    					{
        					MethodProvider.sleepUntil(() -> Trade.isOpen(), 10000);
    					}
    				}
    				else 
    				{
    					Walking.walk(p);
    				}
    			}
    		}
    	}
    	else //got money now :D
    	{
    		//randomize colours then execute missing parts
    		if(!randomized) randomizeColours();
    		if(!API.hasCorrectGender)
        	{
        		genderTransition();
        	}
    		else if(!API.hasCorrectFacialHair)
        	{
        		getFacial();
        	}
        	else if(!API.hasCorrectHair)
        	{
        		getHead();
        	}
        	else if(!API.hasCorrectShirt)
        	{
        		getTopped();
        	}
        	else if(!API.hasCorrectPants)
        	{
        		getBottomed();
        	}
    	}
    	
		return Sleep.calculate(111,111);
    }
    
    private final static Tile thessaliaTile = new Tile(3206, 3416, 0);
    private final static Tile makeoverMageTile = new Tile(2917,3322,0);
    private final static Tile hairdresserTile = new Tile(2949,3379,0);
    private static int hairColourGC = -1;
    private static int topColourGC = -1;
    private static int bottomColourGC = -1;
    private boolean randomized = false;
    private boolean doneWithClothes = false;
    private boolean gotMoney = false;
    
    private void randomizeColours()
    {
    	int rand = API.rand2.nextInt(700);
		if(rand < 100)
		{
			//green color
			hairColourGC = 9;
			topColourGC = 8;
			bottomColourGC = 9;
		} 
		else if(rand < 200)
		{
			//"lumbridge blue" AKA cyan color
			hairColourGC = 8;
			topColourGC = 20;
			bottomColourGC = 20;
		}
		else if(rand < 300)
		{
			//deep blue color
			hairColourGC = 17;
			topColourGC = 21;
			bottomColourGC = 21;
		} 
		else if(rand < 400)
		{
			//deep red color
			hairColourGC = 20;
			topColourGC = 24;
			bottomColourGC = 24;
		}
		else if(rand < 500)
		{
			//yellow color
			hairColourGC = 5;
			topColourGC = 9;
			bottomColourGC = 10;
		} 
		else if(rand < 600)
		{
			//black color
			hairColourGC = 12;
			topColourGC = 16;
			bottomColourGC = 16;
		} else
		{
			//purple color
			hairColourGC = 11;
			topColourGC = 27;
			bottomColourGC = 27;
		}
		randomized = true;
    }
    public static void getHead()
    {
    	
    	if(!Dialogues.inDialogue())
		{
			if(Widgets.getWidgetChild(82,2,2) != null && 
					Widgets.getWidgetChild(82, 2 , 2).isVisible())
			{
				//hair choosing menu is open, click Wild Spikes, then colour, then confirm
				for(String action : Widgets.getWidgetChild(82,2,7).getActions())
				{
					if(action.contains("Wild spikes"))
					{
						if(Widgets.getWidgetChild(82, 2 , 7).interact("Wild spikes"))
						{
							Sleep.sleep(111,111);
							if(Widgets.getWidgetChild(82, 8,hairColourGC).interact())
		    				{
								Sleep.sleep(111,111);
								if(Widgets.getWidgetChild(82, 9).interact("Confirm"))
		        				{
									MethodProvider.log("Confirmed change of hair style/colour");
									API.hasCorrectHair = true;
		        				}
		    				}
						}
						break;
					}
					else
					{
						if(Widgets.getWidgetChild(82, 1,11).interact("Close"))
        				{
							MethodProvider.log("Inside wrong hair screen: facial hair, but need head hair, closed");
        				}
						break;
					}
				}
				Sleep.sleep(111,111);
				return;
			}
			Sleep.sleep(666,333);
			if(!Dialogues.inDialogue())
			{
				talkToHairdresser();
			}
		}
		if(Dialogues.inDialogue())
		{
			//if facial hair option exists choose it
			if(Dialogues.areOptionsAvailable() && Dialogues.chooseOption("Change my hairstyle."))
			{
				Sleep.sleep(667,111);
				return;
			}
		}
    }
    public static void getFacial()
    {
    	if(!Dialogues.inDialogue())
		{
			if(Widgets.getWidgetChild(82,2,2) != null && 
					Widgets.getWidgetChild(82, 2 , 2).isVisible())
			{
				//facial hair choosing menu is open, click Long, then colour, then confirm
				for(String action : Widgets.getWidgetChild(82,2,1).getActions())
				{
					if(action.contains("Goatee"))
					{
						if(Widgets.getWidgetChild(82, 2 , 22).interact("Long"))
						{
							Sleep.sleep(111,111);
							if(Widgets.getWidgetChild(82, 8,hairColourGC).interact())
		    				{
								Sleep.sleep(111,111);
								if(Widgets.getWidgetChild(82, 9).interact("Confirm"))
		        				{
									MethodProvider.log("Confirmed change of facial-hair style/colour");
									API.hasCorrectFacialHair = true;
		        				}
		    				}
						}
						break;
					}
					else
					{
						if(Widgets.getWidgetChild(82, 1,11).interact("Close"))
        				{
							MethodProvider.log("Inside wrong hair screen: head hair, but need facial hair, closed");
        				}
						break;
					}
				}
				Sleep.sleep(111,111);
				return;
			}
			Sleep.sleep(666,333);
			if(!Dialogues.inDialogue())
			{
				talkToHairdresser();
			}
		}
		if(Dialogues.inDialogue())
		{
			//if facial hair option exists choose it
			if(Dialogues.areOptionsAvailable() && Dialogues.chooseOption("Change my facial hair."))
			{
				Sleep.sleep(666,111);
				return;
			}
		}
    }
    public static void genderTransition()
    {
    	if(!Dialogues.inDialogue())
		{
			if(Widgets.getWidgetChild(205,2) != null && 
					Widgets.getWidgetChild(205,2).isVisible())
			{
				//gender choosing menu is open, click male, then PURPLE!!, then confirm
				if(Widgets.getWidgetChild(205,2).interact("Male"))
				{
					Sleep.sleep(111,111);
					if(Widgets.getWidgetChild(205,9,12).interact("Select colour"))
    				{
						Sleep.sleep(111,111);
						if(Widgets.getWidgetChild(205,10).interact("Confirm"))
        				{
							MethodProvider.log("Confirmed change of gender");
							API.hasCorrectGender = true;
        				}
    				}
				}
				Sleep.sleep(111,111);
				return;
			}
			Sleep.sleep(666,333);
			if(!Dialogues.inDialogue())
			{
				talkToMakeoverMage();
			}
		}
		if(Dialogues.inDialogue())
		{
			//verification of changes via chatbox just in case
			if(Dialogues.getNPCDialogue().contains("it seems that spell finally worked okay!") || 
					Dialogues.getNPCDialogue().contains("Woah!") || 
					Dialogues.getNPCDialogue().contains("Whew! That was lucky!"))
			{
				API.hasCorrectGender = true;
				Dialogues.continueDialogue();
				Sleep.sleep(666,111);
				return;
			}
			//if gender option exists choose it
			if(Dialogues.areOptionsAvailable() && Dialogues.chooseOption("Pay 3,000 coins for a makeover."))
			{
				Sleep.sleep(666,111);
				return;
			}
		}
    }
    public static void getBottomed()
    {
    	if(!Dialogues.inDialogue())
		{
			if(Widgets.getWidgetChild(591,7,5) != null && 
					Widgets.getWidgetChild(591,7,5).isVisible())
			{
				//top choosing menu is open, click body style "plain", arm style "Large cuffed", colour, then confirm
				if(Widgets.getWidgetChild(591,7,5).interact("Beach"))
				{
					Sleep.sleep(111,111);
					if(Widgets.getWidgetChild(591,13,bottomColourGC).interact())
    				{
						Sleep.sleep(111,111);
						if(Widgets.getWidgetChild(591,14).interact("Confirm"))
        				{
							MethodProvider.log("Confirmed change of bottom");
							API.hasCorrectPants = true;
        				}
    				}
				}
				Sleep.sleep(111,111);
				return;
			}
			Sleep.sleep(666,333);
			if(!Dialogues.inDialogue())
			{
				talkToThessalia();
			}
		}
		if(Dialogues.inDialogue())
		{
			//if top option exists choose it
			if(Dialogues.areOptionsAvailable() && Dialogues.chooseOption("Change your leggings."))
			{
				Sleep.sleep(666,111);
				return;
			}
		}
    }
    public static void getTopped()
    {
    	if(!Dialogues.inDialogue())
		{
			if(Widgets.getWidgetChild(591,3,16) != null && 
					Widgets.getWidgetChild(591,3,16).isVisible())
			{
				//top choosing menu is open, click body style "plain", arm style "Large cuffed", colour, then confirm
				if(Widgets.getWidgetChild(591,3,16).interact("Plain"))
				{
					Sleep.sleep(111,111);
					if(Widgets.getWidgetChild(591,5,23).interact("Large cuffed"))
					{
						Sleep.sleep(111,111);
						if(Widgets.getWidgetChild(591,13,topColourGC).interact())
	    				{
							Sleep.sleep(111,111);
							if(Widgets.getWidgetChild(591,14).interact("Confirm"))
	        				{
								MethodProvider.log("Confirmed change of top");
								API.hasCorrectShirt = true;
	        				}
	    				}
					}
				}
				Sleep.sleep(111,111);
				return;
			}
			Sleep.sleep(666,333);
			if(!Dialogues.inDialogue())
			{
				talkToThessalia();
			}
		}
		if(Dialogues.inDialogue())
		{
			//if top option exists choose it
			if(Dialogues.areOptionsAvailable() && Dialogues.chooseOption("Change your top."))
			{
				Sleep.sleep(666,111);
				return;
			}
		}
    }
    public static void talkToThessalia()
    {
    	if(Walkz.walkToTileInRadius(thessaliaTile, 8))
		{
			NPC dresser = NPCs.closest("Thessalia");
			if(dresser.canReach())
			{
				if(dresser.interact("Makeover"))
				{
					MethodProvider.sleepUntil(Dialogues::inDialogue, Sleep.calculate(8500, 1111));
					Sleep.sleep(111,333);
				}
				else
				{
					MethodProvider.log("Thessalia talk-to event not executed!");
				}
			}
			else {
				
				if(GameObjects.closest(p -> p.getName().contains("Door") && 
						p.getTile().equals(new Tile(3209, 3415,0))) != null)
				{
					Filter<GameObject> filter = p -> p.getName().contains("Door") && 
							p.getTile().equals(new Tile(3209, 3415,0));
					if(GameObjects.closest(filter).interact("Open"))
					{
						Sleep.sleep(666,333);
						MethodProvider.sleepUntil(() -> !Players.localPlayer().isMoving(), 5000);
					}
				}
				Walking.walk(thessaliaTile);
			}
		}
    }
    public static void talkToMakeoverMage()
    {
    	if(Walkz.walkToTileInRadius(makeoverMageTile, 8))
		{
			NPC dresser = NPCs.closest("Makeover Mage");
			if(dresser.canReach())
			{
				if(dresser.interact("Makeover"))
				{
					MethodProvider.sleepUntil(Dialogues::inDialogue, Sleep.calculate(8500, 1111));
					Sleep.sleep(111,333);
				}
				else
				{
					MethodProvider.log("Makeover Mage talk-to event not executed!");
				}
			}
			else {
				if(GameObjects.closest(p -> p.getName().contains("Door") && 
						p.getTile().equals(new Tile(2922, 3323,0))) != null)
				{
					Filter<GameObject> filter = p -> p.getName().contains("Door") && 
							p.getTile().equals(new Tile(2922, 3323,0));
					if(GameObjects.closest(filter).interact("Open"))
					{
						Sleep.sleep(666,333);
						MethodProvider.sleepUntil(() -> !Players.localPlayer().isMoving(), 5000);
					}
				}
				Walking.walk(makeoverMageTile);
			}
		}
    }
    public static void talkToHairdresser()
    {
    	if(Walkz.walkToTileInRadius(hairdresserTile, 8))
		{
			NPC dresser = NPCs.closest("Hairdresser");
			if(dresser.canReach())
			{
				if(dresser.interact("Haircut"))
				{
					MethodProvider.sleepUntil(Dialogues::inDialogue, Sleep.calculate(8500, 1111));
					Sleep.sleep(111,333);
				}
				else
				{
					MethodProvider.log("Hairdresser talk-to event not executed!");
				}
			}
			else {
				if(GameObjects.closest(p -> p.getName().contains("Door") && 
						p.getTile().equals(new Tile(2949, 3379,0))) != null)
				{
					Filter<GameObject> filter = p -> p.getName().contains("Door") && 
							p.getTile().equals(new Tile(2949, 3379,0));
					if(GameObjects.closest(filter).interact("Open"))
					{
						Sleep.sleep(666,333);
						MethodProvider.sleepUntil(() -> !Players.localPlayer().isMoving(), 5000);
					}
				}
				Walking.walk(hairdresserTile);
			}
		}
    }
}