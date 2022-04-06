import React, {Component} from 'react';

import './AddDNCPhonesModal.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session'

import CircularProgress from '@material-ui/core/CircularProgress';

class AddDNCPhonesModal extends Component {
  constructor(props) {
    super(props);

    this.state = {phones: [], loading: false};
    this.saveDNCPhones = this.saveDNCPhones.bind(this);
  }

  saveDNCPhones() {
    var phones = this.state.phones;
    var dncPhones = phones.split(',');

    const request = {
      userId: Session.getUser().id,
      listId: this.props.selectedList.id,
      phones: dncPhones
    };

    var that = this;
    this.setState({loading: true});

    CampaignsAPI.addDNCPhones(request,
    response => {
      that.setState({loading: false});
      that.props.close();
    });
  }

  onTextFieldChanged(event, field) {
    this.setState({
      [field]: event.target.value
    });
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.close}
        aria-labelledby="form-dialog-title"
        className="custom-dnc-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Add DNC phones</DialogTitle>

        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            id="phones"
            label="Enter DNC phones separated by comma"
            type="text"
            rowsMax="5"
            value={this.state.phones}
            onChange={(e) => this.onTextFieldChanged(e, "phones")}
            fullWidth
            multiline
          />

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.close()} color="primary">
            Cancel
          </Button>
          <Button
            disabled={this.state.phones.length === 0 || this.state.loading}
            onClick={(e) => this.saveDNCPhones()} color="primary">
            Add Phones
          </Button>

          {this.state.loading &&
            <CircularProgress className="custom-progress-dnc-details"/>
          }
        </DialogActions>
      </Dialog>
    )
  }
}

export default AddDNCPhonesModal;
