import React, {Component} from 'react';

import './UserAddFundModal.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import CampaignsAPI from '../campaigns/CampaignsAPI';

class UserAddFundModal extends Component {
  constructor(props) {
    super(props);

    this.state = {value: '', valueError: false};
  }

  addFund() {
    const request = {
      value: this.state.value,
      userId: this.props.user.id
    }

    this.setState({valueError: false});

    if (!request.value) {
      this.setState({valueError: true});
    }

    if (request.value) {
      CampaignsAPI.addFund(request,
      response => {
        if (response.status === 'OK') {
          this.props.handleClose(true);
        }
      });
    }
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
        className="custom-fund-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Add fund to user</DialogTitle>

        <DialogContent>
          <TextField
            autoFocus
            error={this.state.valueError}
            margin="dense"
            id="value"
            label="Enter the amount"
            type="text"
            value={this.state.value}
            onChange={(e) => this.onTextFieldChanged(e, "value")}
            fullWidth
          />

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.addFund()} color="primary">
            Add fund
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default UserAddFundModal;
