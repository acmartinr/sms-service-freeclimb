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

import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';

class PhoneDetailsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      id: this.props.selectedPhone.id,
      forwarding: this.props.selectedPhone.forwarding ? this.props.selectedPhone.forwarding : '',
      note: this.props.selectedPhone.note ? this.props.selectedPhone.note : '',
      tollFree: this.props.selectedPhone.tollFree
    };

    this.handleTollFreeChange = this.handleTollFreeChange.bind(this);
  }

  updatePhone() {
    const request = {
      id: this.state.id,
      forwarding: this.state.forwarding,
      note: this.state.note,
      tollFree: this.state.tollFree
    }

    CampaignsAPI.updatePhone(request,
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

  handleTollFreeChange(event) {
    this.setState({tollFree: event.target.checked});
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
          id="form-dialog-title">Edit Caller ID</DialogTitle>

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

          <TextField
            autoFocus
            fullWidth
            margin="dense"
            id="forwarding"
            label="Enter note"
            type="text"
            multiline
            rowsMax="2"
            value={this.state.note}
            onChange={(e) => this.onTextFieldChanged(e, "note")}
          />

          {Session.getUser().username === 'admin' && <FormControlLabel
            className='toll-free-number-wrapper'
            control={<Checkbox
              color="default"
              onChange={this.handleTollFreeChange}
              checked={this.state.tollFree}/>} label="Toll Free Number" />}

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

export default PhoneDetailsModal;
