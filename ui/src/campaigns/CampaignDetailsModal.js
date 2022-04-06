import React, {Component} from 'react';

import './CampaignDetailsModal.css';
import '../common/Common.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session'

//import Select from '@material-ui/core/Select';
//import MenuItem from '@material-ui/core/MenuItem';
//import InputLabel from '@material-ui/core/InputLabel';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import { MenuItem, Select } from '@material-ui/core';

//import FormControlLabel from '@material-ui/core/FormControlLabel';

/*import DateFnsUtils from '@date-io/date-fns';
import {
  MuiPickersUtilsProvider,
  KeyboardTimePicker,
  KeyboardDatePicker,
} from '@material-ui/pickers';*/

class CampaignDetailsModal extends Component {
  constructor(props) {
    super(props);

    var state = {};
    if (props.selectedCampaign && props.selectedCampaign.id) {
      state = {
        campaigntype: props.selectedCampaign.campaigntype,
        name: props.selectedCampaign.name,
        id: props.selectedCampaign.id,
        title: 'Update Campaign',
        filterDNC: props.selectedCampaign.filterDNC,
        message: props.selectedCampaign.message,
        messageTitle: 'Message [characters left: ' + (160 - props.selectedCampaign.message.length) + ']',
        nameError: false,
        nameUniqueError: false,
        senderCriteria: props.selectedCampaign.senderType,
        submitButtonLabel: 'Update',
        sender: props.selectedCampaign.senderType === 0 ? props.selectedCampaign.senderGroup : 0,
        group: props.selectedCampaign.senderType === 1 ? props.selectedCampaign.senderGroup : 0,
        startDate: new Date(props.selectedCampaign.startDate),
        endDate: new Date(props.selectedCampaign.endDate),
        startTime: new Date(props.selectedCampaign.startTime),
        endTime: new Date(props.selectedCampaign.endTime),
        days: [
          {title: 'monday', value: 'monday', selected: props.selectedCampaign.monday},
          {title: 'tuesday', value: 'tuesday', selected: props.selectedCampaign.tuesday},
          {title: 'wednesday', value: 'wednesday', selected: props.selectedCampaign.wednesday},
          {title: 'thursday', value: 'thursday', selected: props.selectedCampaign.thursday},
          {title: 'friday', value: 'friday', selected: props.selectedCampaign.friday},
          {title: 'saturday', value: 'saturday', selected: props.selectedCampaign.saturday},
          {title: 'sunday', value: 'sunday', selected: props.selectedCampaign.sunday}
        ]};
    } else {
      state = {
        name: '', phone: '', id: 0,
        title: 'New Campaign',
        filterDNC: false,
        message: '',
        messageTitle: 'Message [characters left: 160]',
        nameError: false,
        nameUniqueError: false,
        senderCriteria: 0,
        submitButtonLabel: 'Add',
        sender: 0,
        group: 0,
        startDate: new Date(),
        endDate: new Date(),
        startTime: new Date('01.01.1970 08:00'),
        endTime: new Date('01.01.1970 20:00'),
        days: [
          {title: 'monday', value: 'monday', selected: true},
          {title: 'tuesday', value: 'tuesday', selected: true},
          {title: 'wednesday', value: 'wednesday', selected: true},
          {title: 'thursday', value: 'thursday', selected: true},
          {title: 'friday', value: 'friday', selected: true},
          {title: 'saturday', value: 'saturday', selected: false},
          {title: 'sunday', value: 'sunday', selected: false}
        ]};
    }

    var common = {
      senders: [],
      groups: [],
      messageError: false,
      phones: [],
      selectedPhones: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      forbiddenWords: ['loan','cash','$','!',/*'student',*/'debt', 'cost', 'price', 'gov', 'fed', 'covid', 'corona', 'virus', 'pandemic', 'crisis', 'quarantine', 'stimulus'],
      forbiddenMessage: '',
      allPhones: false,
      populateOrder: 1
    }

    for (var prop in common) {
      state[prop] = common[prop];
    }
    this.state = state;

    //this.handleChange = this.handleChange.bind(this);
    //this.updateSenders = this.updateSenders.bind(this);
    //this.updateSenderGroups = this.updateSenderGroups.bind(this);

    //this.handleStartDateChanged = this.handleStartDateChanged.bind(this);
    //this.handleEndDateChanged = this.handleEndDateChanged.bind(this)

    //this.handleStartTimeChanged = this.handleStartTimeChanged.bind(this);
    //this.handleEndTimeChanged = this.handleEndTimeChanged.bind(this);

    this.onCheckChanged = this.onCheckChanged.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.handleChangePage = this.handleChangePage.bind(this);
  }

  componentDidMount() {
    this.updatePhones();
    this.updateSelectedPhones();

    //this.updateSenders();
    //this.updateSenderGroups();
  }

  updateSelectedPhones() {
    if (this.state.id) {
      CampaignsAPI.getSelectedPhones({id: this.state.id},
      response => {
        if (response.status === "OK") {
          this.setState({
            selectedPhones: response.data
          });
        }
      });
    }
  }

  updatePhones() {
    const request = {
      userId: Session.getUser().id,
      page: 0,
      limit: 1000
    };

    CampaignsAPI.getPhones(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          phones: response.data.phones,
          count: response.data.count
        });

        if (this.state.allPhones) {
          this.setState({selectedPhones: response.data.phones});
        }
      }
    });
  }

  onCheckChanged(event, phone) {
    if (phone === 'all') {
      var value = event.target.checked;
      this.setState({allPhones: value});

      if (value) {
        this.setState({'selectedPhones': this.state.phones.slice()});
      } else {
        this.setState({'selectedPhones': []});
      }

      return;
    }

    var selectedPhones = this.state.selectedPhones.slice();

    if (event.target.checked) {
      selectedPhones.push(phone);
    } else {
      selectedPhones.splice(this.getPhoneIndex(phone, selectedPhones), 1);
      this.setState({allPhones: false});
    }

    this.setState({'selectedPhones': selectedPhones});
  }

  getPhoneIndex(phone, selectedPhones) {
    for (var i = 0; i < selectedPhones.length; i++) {
      if (phone.id === selectedPhones[i].id) {
        return i;
      }
    }

    return 0;
  }

  /*updateSenders() {
    const request = {
      userId: Session.getUser().id,
      page: 0,
      limit: 1000
    };

    CampaignsAPI.getSenders(request,
    response => {
      if (response.status === "OK") {
        if (response.data.senders.length > 0 && !this.state.sender) {
          this.setState({sender: response.data.senders[0].id});
        }

        this.setState({
          senders: response.data.senders
        });
      }
    });
  }

  updateSenderGroups() {
    const request = {
      userId: Session.getUser().id,
      page: 0,
      limit: 1000
    };

    CampaignsAPI.getSenderGroups(request,
    response => {
      if (response.status === "OK") {
        if (response.data.groups.length > 0 && !this.state.group) {
          this.setState({group: response.data.groups[0].id});
        }

        this.setState({
          groups: response.data.groups
        });
      }
    });
  }

  handleStartDateChanged(date) {
    this.setState({startDate: date});
  }
  handleEndDateChanged(date) {
    this.setState({endDate: date});
  }

  handleStartTimeChanged(date) {
    this.setState({startTime: date});
  }
  handleEndTimeChanged(date) {
    this.setState({endTime: date});
  }*/

  startDay(date) {
    return new Date(date.toDateString(date));
  }

  addNewCampaign() {
    const campaign = {
      name: this.state.name,
      message: this.state.message,
      selectedPhones: this.state.selectedPhones,
      allPhones: this.state.allPhones,
      senderType: this.state.senderCriteria,
      senderGroup: this.state.senderCriteria === 0 ? this.state.sender : this.state.group,
      id: this.state.id,
      userId: Session.getUser().id,
      startDate: this.startDay(this.state.startDate).getTime(),
      endDate: this.startDay(this.state.endDate).getTime() + (24 * 60 * 60 * 1000 - 1),
      startTime: this.state.startTime.getTime(),
      endTime: this.state.endTime.getTime(),
      monday: this.state.days[0].selected,
      tuesday: this.state.days[1].selected,
      wednesday: this.state.days[2].selected,
      thursday: this.state.days[3].selected,
      friday: this.state.days[4].selected,
      saturday: this.state.days[5].selected,
      sunday: this.state.days[6].selected,
      filterDNC: this.state.filterDNC,
      campaigntype: this.state.campaigntype
    }

    this.setState({nameUniqueError: false, senderError: false, messageError: false, forbiddenMessage: ''});

    if (campaign.name.length === 0) {
      this.setState({nameError: true});
    }

    if (campaign.message.length === 0) {
      this.setState({messageError: true});
    }

    if (campaign.selectedPhones.length === 0) {
      this.setState({senderError: true});
    }

    var forbiddenWords = [];
    for (var i = 0; i < this.state.forbiddenWords.length; i++) {
      if (this.state.message.toLowerCase().indexOf(this.state.forbiddenWords[i].toLowerCase()) !== -1) {
        forbiddenWords.push(this.state.forbiddenWords[i]);
      }
    }

    if (forbiddenWords.length > 0) {
      var words = '';
      for (var j = 0; j < forbiddenWords.length; j++) {
        words = words + "\"" + forbiddenWords[j] + "\"";
        if (j < forbiddenWords.length - 1) {
          words = words + ', ';
        }
      }

      this.setState({forbiddenMessage:
        'You use forbidden words in your message: ' + words +
        '. Please avoid using these words or your campaign can be banned by telecom operators.'});
    }

    if (campaign.name.length > 0 && campaign.message.length > 0 &&
        campaign.selectedPhones.length > 0 && forbiddenWords.length === 0) {
      CampaignsAPI.createCampaign(campaign,
      response => {
        if (response.status === 'OK') {
          this.props.handleClose(true);
        } else {
          if (response.message.indexOf("name") !== -1) {
            this.setState({nameUniqueError: true});
          }
        }
      });
    }
  }

  onTextFieldChanged(event, field) {
    this.setState({
      nameError: false,
      [field]: event.target.value
    });

    if (field === "message") {
      this.updateLeftSymbolsCount(event.target.value);
    }
  }

  updateLeftSymbolsCount(message) {
     this.setState({messageTitle: 'Message [characters left: ' + (160 - message.length) + ']'});
  }

  selectedPhonesContainPhone = function(phone) {
    for (var i = 0; i < this.state.selectedPhones.length; i++) {
      if (phone.id === this.state.selectedPhones[i].id) {
        return true;
      }
    }

    return false;
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updatePhones);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updatePhones);
  }

  addPopulateTag() {
    const textArea = document.getElementById('campaign-message');
    const textAreaPosition = textArea.selectionStart;

    const message =
      this.state.message.substr(0, textAreaPosition) +
      '<POPULATE' + this.state.populateOrder + '>' +
      this.state.message.substr(textAreaPosition, this.state.message.length);

    if (this.state.populateOrder === 1) {
      this.setState({populateOrder: 2});
    }

    if (message.length < 160) {
      this.setState({'message': message});
      this.updateLeftSymbolsCount(message);
    }

    textArea.focus();
  }
  changeCampaignType(value) {
    this.setState({campaigntype: value});
   }
  /*handleChange(event) {
    this.setState({
      senderError: false,
      [event.target.name]: event.target.value
    });
  }

  onCheckChanged(event, field) {
    const days = this.state.days.slice();
    for (var i = 0; i < days.length; i++) {
      if (days[i].title === field) {
        days[i].selected = event.target.checked;
        break;
      }
    }

    this.setState({'days': days});
  }*/

  onFilterDNCCheckChanged(event, field) {
    this.setState({'filterDNC': event.target.checked});
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-campaign-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          <div className='space-10'/>

          <TextField
            autoFocus
            error={this.state.nameError}
            margin="dense"
            id="name"
            label="Campaign Name"
            type="text"
            value={this.state.name}
            onChange={(e) => this.onTextFieldChanged(e, "name")}
            fullWidth
          />

          {this.state.nameUniqueError &&
            <Typography color="error" component="h5" variant="subtitle1">
              A campaign with entered name is already exists
            </Typography>
          }

          <div className='space-20'/>

          <Button
            className='populate-button'
            onClick={(e) => this.addPopulateTag()} color="primary">
            Populate
          </Button>

          <Select
                      labelId="demo-simple-select-label"
                      id="demo-simple-select"
                      value={this.state.campaigntype}
                      label="Select type"
                      onChange={(event) => {
                        this.changeCampaignType(event.target.value)
                      }}
                    >
                      <MenuItem value={"SMS"}>SMS</MenuItem>
                      <MenuItem value={"VOICE"}>VOICE BROADCAST</MenuItem>
                      <MenuItem value={"VOICE_SMS"}>VOICE and SMS</MenuItem>
                    </Select>

          <TextField
            className="full-width"
            label={this.state.messageTitle}
            error={this.state.messageError || this.state.forbiddenMessage.length > 0}
            id="campaign-message"
            inputProps={{maxLength: 160}}
            onChange={(e) => this.onTextFieldChanged(e, "message")}
            value={this.state.message}
            multiline
            rowsMax="4"
          />

          {this.state.forbiddenMessage.length > 0 &&
            <Typography color="error" className="margin-top-10" component="h5" variant="subtitle1">
              {this.state.forbiddenMessage}
            </Typography>
          }

          {/*<div className='space-20'/>
          <FormControlLabel className='filter-dnc'
            control={
              <Checkbox
                checked={this.state.filterDNC}
                onChange={(e) => this.onFilterDNCCheckChanged(e, 'filterDNC')}
                value={this.state.allPhones}
                color="primary"
              />
            }
            label="Filter Master DNC"
            labelPlacement="start"
          />*/}

          <div className='space-20'/>
          <Typography className="select-phones-label" color={this.state.senderError ? 'error': 'initial'} component="h3">
            Select caller IDs:
          </Typography>

          <div className='select-all-wrapper'>
            <FormControlLabel
                control={
                  <Checkbox
                    checked={this.state.allPhones}
                    onChange={(e) => this.onCheckChanged(e, 'all')}
                    value={this.state.allPhones}
                    color="primary"
                  />
                }
                label="Select all"
                labelPlacement="start"
              />
          </div>

          <div className='table-wrapper'>
            <Table size="small">
              <TableBody>
                {this.state.phones.map(row => (
                  <TableRow key={row.id}>
                    <TableCell className='checkbox-cell'>
                      <Checkbox
                        color="primary"
                        checked={this.selectedPhonesContainPhone(row)}
                        onChange={(e) => this.onCheckChanged(e, row)}/>
                    </TableCell>
                    <TableCell>+{row.phone}</TableCell>
                    <TableCell>{!row.note ? "" : "[" + row.note + "]"}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          <TablePagination
            rowsPerPageOptions={[10, 25, 50]}
            component="div"
            count={this.state.count}
            rowsPerPage={this.state.rowsPerPage}
            page={this.state.page}
            backIconButtonProps={{
              'aria-label': 'Previous Page',
            }}
            nextIconButtonProps={{
              'aria-label': 'Next Page',
            }}
            onChangePage={this.handleChangePage}
            onChangeRowsPerPage={this.handleChangeRowsPerPage}
          />

          {/*<div className='space-20'/>

          <FormControl className='full-width'>
            <InputLabel htmlFor="senderCriteria">Sender Criteria</InputLabel>
            <Select
              value={this.state.senderCriteria}
              onChange={this.handleChange}
              inputProps={{name: 'senderCriteria', id: 'senderCriteria'}}>
              <MenuItem value={0}>Single Sender</MenuItem>
              <MenuItem value={1}>Sender Group</MenuItem>
            </Select>
          </FormControl>

          <div className='space-20'/>
          {this.state.senderCriteria === 0 &&
            <FormControl className='full-width'>
              <InputLabel htmlFor="sender">Sender</InputLabel>
              <Select
                error={this.state.senderError}
                value={this.state.sender}
                onChange={this.handleChange}
                inputProps={{name: 'sender', id: 'sender'}}>
                {this.state.senders.map(row => (
                  <MenuItem key={row.id} value={row.id}>{row.name}</MenuItem>
                ))}
              </Select>
            </FormControl>
          }

          {this.state.senderCriteria === 1 &&
            <FormControl className='full-width'>
              <InputLabel htmlFor="group">Sender Group</InputLabel>
              <Select
                error={this.state.senderError}
                value={this.state.group}
                onChange={this.handleChange}
                inputProps={{name: 'group', id: 'group'}}>
                {this.state.groups.map(row => (
                  <MenuItem key={row.id} value={row.id}>{row.name}</MenuItem>
                ))}
              </Select>
            </FormControl>
          }

          <div className='space'/>
          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              disableToolbar
              variant="inline"
              className="width-50 inline padding-right-10"
              format="MM/dd/yyyy"
              margin="normal"
              id="date-picker-inline"
              label="Start date"
              value={this.state.startDate}
              onChange={this.handleStartDateChanged}
              KeyboardButtonProps={{
                'aria-label': 'change date',
              }}/>

            <KeyboardDatePicker
              disableToolbar
              variant="inline"
              className="width-50 inline"
              format="MM/dd/yyyy"
              margin="normal"
              id="date-picker-inline"
              label="End date"
              value={this.state.endDate}
              onChange={this.handleEndDateChanged}
              KeyboardButtonProps={{
                'aria-label': 'change date',
              }}/>

            <div className='space'/>
            <KeyboardTimePicker
              margin="normal"
              id="time-picker"
              className="width-50 inline padding-right-10"
              label="Start time"
              value={this.state.startTime}
              onChange={this.handleStartTimeChanged}
              KeyboardButtonProps={{
                'aria-label': 'change time',
              }}/>

            <KeyboardTimePicker
              margin="normal"
              id="time-picker"
              className="width-50 inline"
              label="End time"
              value={this.state.endTime}
              onChange={this.handleEndTimeChanged}
              KeyboardButtonProps={{
                'aria-label': 'change time',
              }}/>
          </MuiPickersUtilsProvider>

          <div className='space'/>
          {this.state.days.map(row => (
            <FormControlLabel key={row.value} control={
              <Checkbox
                color="primary"
                checked={row.selected}
                onChange={(e) => this.onCheckChanged(e, row.value)}/>} label={row.title} />
          ))}*/}
        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.addNewCampaign()} color="primary">
            {this.state.submitButtonLabel}
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default CampaignDetailsModal;
