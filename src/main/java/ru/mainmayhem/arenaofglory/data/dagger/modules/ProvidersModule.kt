package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Binds
import dagger.Module
import javax.inject.Singleton
import ru.mainmayhem.arenaofglory.resources.strings.StringsResource
import ru.mainmayhem.arenaofglory.resources.strings.StringsResourceImpl
import ru.mainmayhem.arenaofglory.resources.strings.source.BuyerStringsSource
import ru.mainmayhem.arenaofglory.resources.strings.source.StringSource


@Module
abstract class ProvidersModule {

    //todo добавить сюда метод bindStringsResource
    @Binds
    @Singleton
    abstract fun bindStringsSource(impl: BuyerStringsSource): StringSource

    @Binds
    @Singleton
    abstract fun bindStringsResource(impl: StringsResourceImpl): StringsResource
}