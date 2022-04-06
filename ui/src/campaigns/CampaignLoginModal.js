import React, {Component} from 'react';

import './CampaignLoginModal.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session'
import md5 from 'md5';
import Typography from '@material-ui/core/Typography';

class CampaignLoginModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      agentUsername: props.selectedCampaign.agentUsername ? props.selectedCampaign.agentUsername : '',
      agentPassword: '',
      id: props.selectedCampaign.id,
      agentUsernameError: false
    };
  }

  updateAgentCredentials() {
    const request = {
      userId: Session.getUser().id,
      id: this.props.selectedCampaign.id,
      agentUsername: this.state.agentUsername,
      agentPassword: this.state.agentPassword ? md5(this.state.agentPassword) : undefined
    };

    this.setState({agentUsernameError: false});

    CampaignsAPI.updateAgentCredentials(request,
    response => {
      if (response.status === "OK") {
        this.props.handleClose();
      } else {
        this.setState({agentUsernameError: true});
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
        className="custom-campaign-login-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Agent credentials</DialogTitle>

        <DialogContent>
          <div className='space-10'/>

          <TextField
            autoFocus
            error={this.state.agentUsernameError}
            margin="dense"
            id="username"
            label="Agent username"
            type="text"
            value={this.state.agentUsername}
            onChange={(e) => this.onTextFieldChanged(e, "agentUsername")}
            fullWidth
          />

          {this.state.agentUsernameError &&
            <Typography color="error" component="h1" variant="subtitle1">
              Agent with this username is already exist. Try another username.
            </Typography>
          }

          <TextField
            margin="dense"
            id="password"
            label="Agent password"
            type="password"
            value={this.state.agentPassword}
            onChange={(e) => this.onTextFieldChanged(e, "agentPassword")}
            fullWidth
          />

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.updateAgentCredentials()} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default CampaignLoginModal;
