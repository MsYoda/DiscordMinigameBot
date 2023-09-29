package org.test.config;

public class BotLanguageConfig {
    // Штуки по типу $(money) - плейсхолдер, который заменится на соответствующее значение внутри обработки команды
    public static final String invalidArgumentsType = "Error! Invalid arguments types. Check them and try again!";
    public static final String invalidArgumentsCount = "Error! Provide all needed arguments.";
    public static final String addMoneySucceed = "Successful add $(money) money to $(user)!";
    public static final String removeMoneySucceed = "Successful remove $(money) money from $(user)!";
    public static final String transferMoneySucceed = "Successful transfer $(money) from $(source-user) to $(destination-user)!";
    public static final String knowBalanceSucceed = "$(user) balance is $(money)!";
    public static final String userDoesntHaveMoney = "$(user) doesnt have enough money!\nHis balance is $(money)!";

    public static final String userDoesntHavePermissions = "You dont have permissions to do this!";

    //-------Commands configs--------

    public static final String moneyParamName = "money";
    public static final String userParamName = "user";

    public static final String sourceUserParamName = "source-" + userParamName;
    public static final String destinationUserParamName = "destination-" + userParamName;
    public static final String knowBalanceCommandName = "know-balance";
    public static final String addMoneyCommandName = "add-money";
    public static final String removeMoneyCommandName = "remove-money";
    public static final String transferMoneyCommandName = "transfer-money";
}
