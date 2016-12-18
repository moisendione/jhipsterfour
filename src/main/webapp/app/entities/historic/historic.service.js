(function() {
    'use strict';
    angular
        .module('jhipsterfourApp')
        .factory('Historic', Historic);

    Historic.$inject = ['$resource', 'DateUtils'];

    function Historic ($resource, DateUtils) {
        var resourceUrl =  'api/historics/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
