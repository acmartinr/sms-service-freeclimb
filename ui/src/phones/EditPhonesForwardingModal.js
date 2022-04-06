import React, {Component} from 'react';

import './EditPhoneModal.css';
import '../common/Common.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

class EditPhonesForwardingModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      forwarding: '',
    };
  }

  updatePhone() {
    const request = {
      forwarding: this.state.forwarding,
      userId: Session.getUser().id
    }

    CampaignsAPI.updatePhonesForwarding(request,
    response => {
      if (response.status === 'OK') {
        this.props.handleClose(true);
      }
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
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-sender-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Update forwarding phone</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            className="margin-bottom-20"
            fullWidth
            margin="dense"
            id="forwarding"
            label="Enter forwarding phone"
            type="text"
            value={this.state.forwarding}
            onChange={(e) => this.onTextFieldChanged(e, "forwarding")}
          />

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>

          <Button
              onClick={(e) => this.updatePhone()} color="primary">
            Update
          </Button>

        </DialogActions>
      </Dialog>
    )
  }
}

export default EditPhonesForwardingModal;
