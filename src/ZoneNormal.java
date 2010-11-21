public class ZoneNormal extends ZoneType {

	public ZoneNormal(int id) {
		super(id);
	}

	@Override
	public void onEnter(Player character) {
		ZoneType zone = World.getInstance().getActiveZone(character);
		if(zone == null || zone.getZone().getSize() > this.getZone().getSize())
			zone = this;

		character.sendMessage("You have just entered " + getName() + "["+zone.getAccess(character).toColorCode()+"].");
		
	}

	@Override
	public void onExit(Player character) {
		character.sendMessage("You have just exited " + getName() + ".");

	}

	@Override
	public boolean allowWater(Block b) {
		return allowWater;
	}

	@Override
	public boolean allowLava(Block b) {
		return allowLava;
	}

	@Override
	public boolean allowDynamite(Block b) {
		return allowDynamite;
	}

}
