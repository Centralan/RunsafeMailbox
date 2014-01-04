package no.runsafe.mailbox.repositories;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

public class MailboxRepository extends Repository
{
	public MailboxRepository(IDatabase database, IServer server)
	{
		this.database = database;
		this.server = server;
	}

	@Override
	public String getTableName()
	{
		return "player_mailboxes";
	}

	public RunsafeInventory getMailbox(IPlayer player)
	{
		String playerName = player.getName();
		RunsafeInventory inventory = server.createInventory(null, 27, String.format("%s's Mailbox", playerName));

		String data = this.database.queryString("SELECT contents FROM player_mailboxes WHERE player = ?", playerName);
		if (data != null)
			inventory.unserialize(data);

		return inventory;
	}

	public void updateMailbox(IPlayer player, RunsafeInventory inventory)
	{
		String contents = inventory.serialize();
		this.database.execute(
			"INSERT INTO player_mailboxes (player, contents) VALUES(?, ?) ON DUPLICATE KEY UPDATE contents = ?",
			player.getName(), contents, contents
		);
	}

	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE `player_mailboxes` (" +
				"`player` varchar(50) NOT NULL," +
				"`contents` longtext," +
				"PRIMARY KEY (`player`)" +
			")"
		);

		return update;
	}

	private final IDatabase database;
	private final IServer server;
}
