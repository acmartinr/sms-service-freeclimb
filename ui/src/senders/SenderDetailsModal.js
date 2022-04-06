import React, {Component} from 'react';

import './SenderDetailsModal.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';

import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session'

class SenderDetailsModal extends Component {
  constructor(props) {
    super(props);

    if (props.selectedSender && props.selectedSender.id) {
      this.state = {
        phones: [],
        name: props.selectedSender.name,
        phone: props.selectedSender.phone,
        id: props.selectedSender.id,
        title: 'Update Sender',
        nameError: false, phoneError: false,
        nameUniqueError: false, phoneUniqueError: false,
        submitButtonLabel: 'Update'};
    } else {
      this.state = {
        phones: [],
        name: '', phone: '', id: 0,
        title: 'New Sender',
        nameError: false, phoneError: false,
        nameUniqueError: false, phoneUniqueError: false,
        submitButtonLabel: 'Add'};
    }

    this.handleChange = this.handleChange.bind(this);
    this.updateFreePhones = this.updateFreePhones.bind(this);
  }

  componentDidMount() {
    this.updateFreePhones();
  }

  updateFreePhones() {
    const request = {
      userId: Session.getUser().id
    }

    CampaignsAPI.getFreePhones(request,
    response => {
      if (response.status === 'OK') {
        if (this.state.phone) {
          response.data.push({id: 0, phone: this.state.phone});
        }

        if (!this.state.phone && response.data.length > 0) {
          this.setState({'phone': response.data[0].phone});
        }

        this.setState({'phones': response.data});
      }
    });
  }

  addNewSender() {
    const sender = {
      name: this.state.name,
      phone: this.state.phone,
      id: this.state.id,
      userId: Session.getUser().id
    }

    this.setState({nameUniqueError: false, phoneUniqueError: false});

    if (sender.name.length === 0) {
      this.setState({nameError: true});
    }

    if (!sender.phone) {
      this.setState({phoneError: true});
    }

    if (sender.name.length > 0 && sender.phone) {
      CampaignsAPI.createSender(sender,
      response => {
        if (response.status === 'OK') {
          this.props.handleClose(true);
        } else {
          if (response.message.indexOf("name") !== -1) {
            this.setState({nameUniqueError: true});
          }

          if (response.message.indexOf("phone") !== -1) {
            this.setState({phoneUniqueError: true});
          }
        }
      });
    }
  }

  onTextFieldChanged(event, field) {
    this.setState({
      nameError: false,
      phoneError: false,
      [field]: event.target.value
    });
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value
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
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          <TextField
            autoFocus
            error={this.state.nameError}
            margin="dense"
            id="name"
            label="Sender Name"
            type="text"
            value={this.state.name}
            onChange={(e) => this.onTextFieldChanged(e, "name")}
            fullWidth
          />

          {this.state.nameUniqueError &&
            <Typography color="error" component="h5" variant="subtitle1">
              A sender with entered name is already exists
            </Typography>
          }

          <div className='space'/>

          <FormControl error={this.state.phoneError} className='full-width'>
            <InputLabel htmlFor="phone">Sender Phone</InputLabel>
            <Select
              value={this.state.phone}
              onChange={this.handleChange}
              inputProps={{name: 'phone', id: 'phone'}}>
              {this.state.phones.map(row => (
                <MenuItem key={row.id} value={row.phone}>+{row.phone}</MenuItem>
              ))}
            </Select>
          </FormControl>

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.addNewSender()} color="primary">
            {this.state.submitButtonLabel}
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default SenderDetailsModal;
