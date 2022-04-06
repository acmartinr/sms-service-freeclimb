import React, {Component} from 'react';

import './CampaignTestSMSModal.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';

import CampaignsAPI from '../campaigns/CampaignsAPI';

import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import CircularProgress from '@material-ui/core/CircularProgress';

class CampaignTestSMSModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
        id: props.selectedCampaign.id,
        title: 'Send Test Message',
        receiverError: false,
        senderError: false,
        serverError: '',
        submitButtonLabel: 'Send',
        selectedPhones: [],
        phoneFrom: '',
        phoneTo: '',
        data: '',
        data2: '',
        loading: false
    }

    this.handleChange = this.handleChange.bind(this);
    this.onTextFieldChanged = this.onTextFieldChanged.bind(this);
  }

  componentDidMount() {
    this.updateSelectedPhones();
  }

  updateSelectedPhones() {
    if (this.state.id) {
      CampaignsAPI.getSelectedPhones({id: this.state.id},
      response => {
        if (response.status === "OK") {
          this.setState({
            selectedPhones: response.data,
            phoneFrom: response.data.length > 0 ? response.data[0].phone : ''
          });
        }
      });
    }
  }

  sendTestMessage() {
    const request = {
      phoneFrom: this.state.phoneFrom,
      phoneTo: this.state.phoneTo,
      campaignId: this.state.id,
      data: this.state.data,
      data2: this.state.data2
    }

    this.setState({receiverError: false, senderError: false, severError: true});

    if (!request.phoneFrom) {
      this.setState({receiverError: true});
    }

    if (!request.phoneTo) {
      this.setState({messageError: true});
    }

    if (request.phoneTo && request.phoneFrom) {
      this.setState({loading: true});

      CampaignsAPI.sendTestSMS(request,
      response => {
        if (response.status === 'OK') {
          this.setState({loading: false});
          this.props.handleClose(true);
        } else {
          if (response.message === 'balance') {
            this.setState({serverError: 'Your balance is too low', loading: false});
          } else {
            this.setState({serverError: response.message, loading: false});
          }
        }
      });
    }
  }

  onTextFieldChanged(event, field) {
    this.setState({
      receiverError: false,
      [field]: event.target.value
    });
  }

  handleChange(event) {
    this.setState({
      senderError: false,
      [event.target.name]: event.target.value
    });
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="test-sms-campaign-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          <div className='space-10'/>

          <TextField
            autoFocus
            error={this.state.receiverError}
            margin="dense"
            id="name"
            label="Receiver Phone Number"
            type="text"
            value={this.state.phoneTo.replace(/[^0-9]/g, '')}
            onChange={(e) => this.onTextFieldChanged(e, "phoneTo")}
            fullWidth
          />

          {this.state.receiverError &&
            <Typography color="error" component="h5" variant="subtitle1">
              Enter receiver phone number
            </Typography>
          }

          {this.state.serverError &&
            <Typography color="error" component="h5" variant="subtitle1">
              {this.state.serverError}
            </Typography>
          }

          <div className='space-10'/>

          <TextField
            margin="dense"
            id="data"
            label="Populated data 1 (optional)"
            type="text"
            value={this.state.data}
            onChange={(e) => this.onTextFieldChanged(e, "data")}
            fullWidth
          />

          <TextField
            margin="dense"
            id="data2"
            label="Populated data 2 (optional)"
            type="text"
            value={this.state.data2}
            onChange={(e) => this.onTextFieldChanged(e, "data2")}
            fullWidth
          />

          <div className='space-20'/>

          <FormControl className='full-width'>
            <InputLabel htmlFor="phoneFrom">Caller ID</InputLabel>
            <Select
              value={this.state.phoneFrom}
              onChange={this.handleChange}
              error={this.senderError}
              inputProps={{name: 'phoneFrom', id: 'phoneFrom'}}>
              {this.state.selectedPhones.map(row => (
                <MenuItem key={row.id} value={row.phone}>+{row.phone}</MenuItem>
              ))}
            </Select>
          </FormControl>
        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>

          <Button disabled={this.state.loading} onClick={(e) => this.sendTestMessage()} color="primary">
            {this.state.submitButtonLabel}
          </Button>

          {this.state.loading && <CircularProgress className="custom-progress"/>}

        </DialogActions>
      </Dialog>
    )
  }
}

export default CampaignTestSMSModal;
