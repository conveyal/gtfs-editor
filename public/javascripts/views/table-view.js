var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var collectionToDatatables = function(collection) {
    var fields = _.keys(new collection.model().defaults),
        aaData = [],
        aoColumns = _.map(fields, function(key) {
          return { sTitle: key };
        });

    _.each(collection.toJSON(), function(obj){
      var rowData = [];
      _.each(fields, function(field) {
        var val = obj[field];

        if (_.isObject(val)) {
          val = val.id || JSON.stringify(val);
        }

        if (field === 'id') {
          val = '<a href="#'+collection.type+'/'+val+'">'+val+'</a>';
        }

        rowData.push(val);
      });
      aaData.push(rowData);
    });

    return {
      aoColumns: aoColumns,
      aaData: aaData
    };
  };

  G.TableView = Backbone.View.extend({
    initialize: function() {
      this.collection.on('reset', this.render, this);
    },
    render: function() {
      this.$el.html(ich['collection-table-tpl'](
        {new_link: '#'+this.collection.type+'/new', title: this.collection.type}
      ));

      if (this.collection.first()) {
        var tableOptions = collectionToDatatables(this.collection, this.collection.type);
        tableOptions.bPaginate = false;
        tableOptions.bInfo = false;

        this.$('#collection-table').show().dataTable(tableOptions);
      }

      return this;
    }
  });
})(GtfsEditor, jQuery, ich);