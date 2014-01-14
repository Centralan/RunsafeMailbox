package no.runsafe.mailbox.commands;

import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.PlayerArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IAmbiguousPlayer;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.mailbox.MailHandler;
import no.runsafe.mailbox.MailSender;

public class SendBook extends PlayerCommand
{
	public SendBook(MailHandler mailHandler, MailSender mailSender)
	{
		super("sendbook", "Sends a book that you are holding", "runsafe.mailbox.send.book", new PlayerArgument());
		this.mailHandler = mailHandler;
		this.mailSender = mailSender;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		IPlayer player = parameters.getPlayer("player");

		if (player == null)
			return "&cThat player does not exist.";

		if (player instanceof IAmbiguousPlayer)
			return player.toString();

		if (player.getName().equalsIgnoreCase(executor.getName()))
			return "&cYou cannot mail things to yourself.";

		if (!this.mailHandler.hasMailBookCost(executor))
			return "&cYou do not have enough money to send a book. Sending books costs " + this.mailHandler.getMailBookCostText() + ".";

		if (!this.mailSender.hasFreeMailboxSpace(player))
			return "&cThat player cannot receive mail right now.";

		RunsafeMeta itemInHand = executor.getItemInHand();
		if (itemInHand == null || !itemInHand.is(Item.Special.Crafted.WrittenBook))
			return "&cYou must be holding a written book.";

		this.mailSender.sendItemInHand(player, executor);
		this.mailHandler.removeMailBookCost(executor);
		return null;
	}

	private final MailHandler mailHandler;
	private final MailSender mailSender;
}
