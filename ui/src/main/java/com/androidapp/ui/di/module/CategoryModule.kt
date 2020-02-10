package io.github.patrickyin.cleanarchitecture.app.articles.di.module

import com.androidapp.navweiandroidv2.di.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
class CategoryModule {
  @PerActivity
  @Provides
  internal fun provideMainPresenter(getGetCategoryUseCase: GetCategoryUseCase) = ArticlesPresenter(getArticlesListUseCase)
}
