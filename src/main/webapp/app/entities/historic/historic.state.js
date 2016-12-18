(function() {
    'use strict';

    angular
        .module('jhipsterfourApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('historic', {
            parent: 'entity',
            url: '/historic?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterfourApp.historic.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/historic/historics.html',
                    controller: 'HistoricController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('historic');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('historic-detail', {
            parent: 'entity',
            url: '/historic/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterfourApp.historic.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/historic/historic-detail.html',
                    controller: 'HistoricDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('historic');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Historic', function($stateParams, Historic) {
                    return Historic.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'historic',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('historic-detail.edit', {
            parent: 'historic-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/historic/historic-dialog.html',
                    controller: 'HistoricDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Historic', function(Historic) {
                            return Historic.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('historic.new', {
            parent: 'historic',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/historic/historic-dialog.html',
                    controller: 'HistoricDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                creationDate: null,
                                username: null,
                                action: null,
                                tableName: null,
                                recordId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('historic', null, { reload: 'historic' });
                }, function() {
                    $state.go('historic');
                });
            }]
        })
        .state('historic.edit', {
            parent: 'historic',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/historic/historic-dialog.html',
                    controller: 'HistoricDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Historic', function(Historic) {
                            return Historic.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('historic', null, { reload: 'historic' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('historic.delete', {
            parent: 'historic',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/historic/historic-delete-dialog.html',
                    controller: 'HistoricDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Historic', function(Historic) {
                            return Historic.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('historic', null, { reload: 'historic' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
