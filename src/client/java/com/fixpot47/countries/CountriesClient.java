package com.fixpot47.countries;

import net.fabricmc.api.ClientModInitializer;

public final class CountriesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CountryCommand.register();
    }
}
