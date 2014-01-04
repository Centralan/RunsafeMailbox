package no.runsafe.mailbox.repositories;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.ITransaction;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MailPackageRepository extends Repository
{
	public MailPackageRepository(IDatabase database, IServer server)
	{
		this.database = database;
		this.server = server;
	}

	@Override
	public String getTableName()
	{
		return "mail_packages";
	}

	public RunsafeInventory getMailPackage(int packageID)
	{
		RunsafeInventory inventory = server.createInventory(null, 54, "");

		String data = this.database.queryString("SELECT contents FROM mail_packages WHERE ID = ?", packageID);
		if (data != null)
			inventory.unserialize(data);

		return inventory;
	}

	public int newPackage(RunsafeInventory contents)
	{
		ITransaction transaction = database.isolate();
		transaction.execute("INSERT INTO mail_packages (contents) VALUES(?)", contents.serialize());
		Integer id = transaction.queryInteger("SELECT LAST_INSERT_ID()");
		transaction.Commit();
		return id == null ? 0 : id;
	}

	public void removePackage(int packageID)
	{
		this.database.execute("DELETE FROM mail_packages WHERE ID = ?", packageID);
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> versions = new LinkedHashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE `mail_packages` (" +
				"`ID` int(10) unsigned NOT NULL AUTO_INCREMENT," +
				"`contents` longtext," +
				"PRIMARY KEY (`ID`)" +
				")"
		);
		versions.put(1, sql);
		return versions;
	}

	private final IDatabase database;
	private final IServer server;
}
