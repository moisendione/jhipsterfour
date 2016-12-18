(function() {
    'use strict';

    angular
        .module('jhipsterfourApp')
        .factory('HistoricSearch', HistoricSearch);

    HistoricSearch.$inject = ['$resource'];

    function HistoricSearch($resource) {
        var resourceUrl =  'api/_search/historics/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
