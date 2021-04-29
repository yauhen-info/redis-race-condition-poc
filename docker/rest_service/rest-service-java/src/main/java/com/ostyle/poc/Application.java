package com.ostyle.poc;

import com.ostyle.poc.rest.ScoreDataApi;
import io.bootique.BaseModule;
import io.bootique.Bootique;
import io.bootique.di.Binder;
import io.bootique.jersey.JerseyModule;

/**
 * Registers all components and starts the REST application.
 */
public class Application extends BaseModule {

    public static void main(String[] args) {
        Bootique.app(args)
                .autoLoadModules()
                .module(Application.class)
                .exec()
                .exit();
    }

    @Override
    public void configure(Binder binder) {
        JerseyModule
                .extend(binder)
                .addResource(ScoreDataApi.class);

    }
}
